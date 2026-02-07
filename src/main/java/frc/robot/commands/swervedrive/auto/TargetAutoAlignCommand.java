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

    // == AUTO-ALIGNMENT CONFIGURATION ==

    // defines which april tags are "left" and "right" april tags so the robot always knows what it's looking for
    int[] leftTagIds = {1};
    int[] rightTagIds = {2};

    final double targetDistance = 15.925449429385568; // the coordinate length value that the robot is trying to reach
    final double targetDistanceError = 0.15; // the amount of error that's allowed when trying to reach the target distance value
    final double driveRubberBandingMultiplier = 1; // robot drive speed when rubberbanding
    final double correctionSpeed = 0.5; // the speed for the robot to drive backwards if it gets too close to the april tags
    
    final double seekingRotationSpeed = 1.5; // the speed the robot rotates at when searching for april tags
    final double rotationalRubberBandingMultiplier = 0.15; // rotational speed when rubberbanding
    final double seekingError = 0.05; // the amount of error that's allowed when trying to align with the april tags
    
    // be careful with these ones
    final double driveSpeedLimit = 0.9; // used when rubberbanding driving (measured in m/s)
    final double rotationalSpeedLimit = 0.8; // used when rotationally rubberbanding, prevents the robot from going psycho (measured in degrees per second)
    





    // == DON'T TOUCH THESE ==

    // current stage of aligning, with 0 being rotating, and 1 being moving back and forth
    // when it equals 2, the robot completes alignment and stops moving
    int currentAligningStage = 0; 



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

    public double getRubberBandingSpeed(double distanceFromCenter, double multiplier, double limit) {
        if (distanceFromCenter > seekingError || distanceFromCenter < seekingError * -1) {
            return clampCalc(distanceFromCenter * multiplier * -1, limit * -1, limit);
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

                drivebase.drive(new Translation2d(0, 0), getRubberBandingSpeed(middle, rotationalRubberBandingMultiplier, rotationalSpeedLimit), false);

                // check if robot is aligned
                if (isApproximatelyEqual(middle, 0, 0.15)) {
                    currentAligningStage = 1;
                }
            } else {
                drivebase.drive(new Translation2d(0, 0), seekingRotationSpeed, false);
            }

        } else {
            TargetAprilTags validAprilTags = retrieveValidTargetTags(getAprilTags().get());


            if (validAprilTags.isValid()) {
                double coordinateDistance = validAprilTags.getTagDistance();

                System.out.println(coordinateDistance);

                if (isApproximatelyEqual(coordinateDistance, targetDistance, targetDistanceError)) {
                    currentAligningStage = 2;
                } else if (coordinateDistance > targetDistance + targetDistanceError) {
                    drivebase.drive(new Translation2d(getRubberBandingSpeed(coordinateDistance - targetDistance, driveRubberBandingMultiplier, driveSpeedLimit), 0), 0.0, false);
                } else {
                    drivebase.drive(new Translation2d(getRubberBandingSpeed(coordinateDistance - targetDistance, driveRubberBandingMultiplier, driveSpeedLimit), 0), 0.0, false);
                }
                
            } else {
                drivebase.drive(new Translation2d(correctionSpeed * -1, 0), 0.0, false);
            }
            
        }

    }

    @Override
    public boolean isFinished() {
        if (currentAligningStage == 2) {
            System.out.println("Aligning complete!");
            return true;
        }
        return false;
    }

@Override
public void end(boolean interrupted) {

}
}
