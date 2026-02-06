package frc.robot.commands.swervedrive.auto;

import java.util.Optional;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import limelight.Limelight;
import limelight.networktables.LimelightData;
import limelight.results.RawFiducial;


public class TargetAutoAlignCommand extends Command {
    public SwerveSubsystem drivebase;
    Limelight limelight = new Limelight("limelight-butler");

    // current stage of aligning, with 0 being rotating, and 1 being moving back and forth
    // when it equals 2, the robot completes alignment and stops moving
    int currentAligningStage = 0; 

    // defines which april tags are on the left and the right to eliminate error
    int[] leftTagIds = {1};
    int[] rightTagIds = {2};

    public TargetAutoAlignCommand(SwerveSubsystem swerveSubsystem) {
        drivebase = swerveSubsystem;
    }

    public boolean intArrayContainsValue(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }

        return false;
    }

    public Optional<RawFiducial[]> getAprilTags() {
        limelight.getSettings().withCameraOffset(Pose3d.kZero);
        LimelightData scanData = limelight.getData();

        if (scanData != null) {
            return Optional.ofNullable(scanData.getRawFiducials()); // get array of april tags
        }

        return null;
    }

    // error = the allowed error in precision when deciding if value2 is "approximately" equal to value1
    public boolean isApproximatelyEqual(double value1, double value2, double error) {
        if (value1 >= value2 - Math.abs(error) && value1 <= value2 + Math.abs(error)) {
            return true;
        }

        return false;
    }

    public double clampCalc(double value, double lowEnd, double highEnd) {
        if (value < lowEnd) {
            return lowEnd;
        } else if (value > highEnd) {
            return highEnd;
        } else {
            return value;
        }
    }

    public TargetAprilTags retrieveValidTargetTags(RawFiducial[] aprilTags) {
        if (aprilTags.length != 2) {
            return new TargetAprilTags();
        }

        TargetAprilTags tags = new TargetAprilTags();
        for (int i = 0; i < 2; i++) {
            if (intArrayContainsValue(leftTagIds, aprilTags[i].id)) {
                tags.setLeftTag(aprilTags[i]);
            }

            if (intArrayContainsValue(rightTagIds, aprilTags[i].id)) {
                tags.setRightTag(aprilTags[i]);
            }
        }

        if (tags.isValid()) {
            return tags;
        } else {
            return null;
        }
    }

    public double getRubberBandingSpeed(double distanceFromCenter) {
        if (distanceFromCenter > 0.15 || distanceFromCenter < -0.15) {
            return clampCalc(distanceFromCenter * -0.15, -0.8, 0.8);
        } else {
            return 0.0;
        }
    }

    @Override
    public void initialize() {
        currentAligningStage = 0;
    }


    // distance to reach: 8.556558354542982
    @Override
    public void execute() {
        if (currentAligningStage == 0) {
            TargetAprilTags validAprilTags = retrieveValidTargetTags(getAprilTags().get());
            if (validAprilTags.isValid()) {
                drivebase.zeroGyro();
                double middle = validAprilTags.getTagMidpoint();

                drivebase.drive(new Translation2d(0, 0), getRubberBandingSpeed(middle), false);

                // check if robot is aligned
                if (isApproximatelyEqual(middle, 0, 0.15)) {
                    currentAligningStage = 1;
                    System.out.println("Aligning stage 1 reached!");
                }
            } else {
                drivebase.drive(new Translation2d(0, 0), 2, false);
            }

        } else {
            TargetAprilTags validAprilTags = retrieveValidTargetTags(getAprilTags().get());
            final double targetDistance = 8.556558354542982;
            final double driveSpeed = 0.15;

            if (validAprilTags.isValid()) {
                double coordinateDistance = validAprilTags.getTagDistance();

                System.out.println(coordinateDistance);

                if (isApproximatelyEqual(coordinateDistance, targetDistance, 0.15)) {
                    System.out.println("Robot finished!");
                    currentAligningStage = 2;
                } else if (coordinateDistance > targetDistance + 0.15) {
                    System.out.println("AprilTags too close! Moving backwards.");
                    drivebase.drive(new Translation2d(driveSpeed * -1, 0), 0.0, false);
                } else {
                    System.out.println("AprilTags too far! Moving forwards.");
                    drivebase.drive(new Translation2d(driveSpeed, 0), 0.0, false);
                }
                
            } else {
                System.out.println("Tags invalid! Moving backwards.");
                drivebase.drive(new Translation2d(driveSpeed * -1, 0), 0.0, false);
            }
            
        }

    }

    @Override
    public boolean isFinished() {
        if (currentAligningStage == 2) {
            return true;
        }
        return false;
    }

@Override
public void end(boolean interrupted) {

}
}
