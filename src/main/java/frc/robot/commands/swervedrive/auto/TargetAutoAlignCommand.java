package frc.robot.commands.swervedrive.auto;

import java.util.Date;
import java.util.Optional;

import edu.wpi.first.apriltag.AprilTag;
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
        if (value1 >= value2 - Math.abs(error) || value1 <= value2 + Math.abs(error)) {
            return true;
        }

        return false;
    }

    public double clampCalc(double value, double lowEnd, double highEnd){
       if(value < lowEnd){
        return lowEnd;
       }
         else if(value > highEnd){
            return highEnd;
        }
        else{
            return value;
        }
    }

    public boolean isValidAprilTags(RawFiducial[] aprilTags) {
        if (aprilTags.length == 2) {
            boolean leftTagValid = false;
            boolean rightTagValid = false;

            for (int i = 0; i< 2; i++) {
                if (intArrayContainsValue(leftTagIds, aprilTags[i].id)) {
                    leftTagValid = true;
                }

                if (intArrayContainsValue(rightTagIds, aprilTags[i].id)) {
                    rightTagValid = true;
                }
            }

            if (leftTagValid && rightTagValid) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public double getRubberBandingSpeed(double distanceFromCenter) {
        if (distanceFromCenter > 0.15 || distanceFromCenter < -0.15) {
            return clampCalc(distanceFromCenter * -0.15, -0.8, 0.8);
        } else {
            return 0.0;
        }
    }

    // CALCULATE DISTANCE THINGY
    public double getCoordinateDistanceBetweenTags(RawFiducial[] apriltags) {

        RawFiducial leftTag = new RawFiducial(0, 0, 0, 0, 0, 0, 0);
        RawFiducial rightTag = new RawFiducial(0, 0, 0, 0, 0, 0, 0);



        return Math.sqrt(Math.pow(rightTag.txnc - leftTag.txnc, 2) + Math.pow(rightTag.tync - leftTag.tync, 2));
    }



    @Override
    public void initialize() {
        currentAligningStage = 0;
    }


    // distance to reach: 8.556558354542982
    @Override
    public void execute() {
        if (currentAligningStage == 0) {
            RawFiducial[] aprilTags = getAprilTags().get();
            if (isValidAprilTags(aprilTags)) {
                drivebase.zeroGyro();
                double middle = (aprilTags[1].txnc + aprilTags[0].txnc) / 2;

                drivebase.drive(new Translation2d(0, 0), getRubberBandingSpeed(middle), false);

                // check if robot is aligned
                if (isApproximatelyEqual(middle, 0, 0.15)) {
                    currentAligningStage = 1;
                }
            } else {
                drivebase.drive(new Translation2d(0, 0), 2, false);
            }

        } else {
            RawFiducial[] aprilTags = getAprilTags().get();
            final double targetDistance = 8.556558354542982;
            final double driveSpeed = 0.1;

            double coordinateDistance = getCoordinateDistanceBetweenTags(aprilTags);

            if (isApproximatelyEqual(coordinateDistance, targetDistance, 0.15)) {
                currentAligningStage = 2;
            } else if (coordinateDistance > targetDistance + 0.15) {
                drivebase.drive(new Translation2d(driveSpeed * -1, 0), 0.0, false);
            } else {
                drivebase.drive(new Translation2d(driveSpeed, 0), 0.0, false);
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
