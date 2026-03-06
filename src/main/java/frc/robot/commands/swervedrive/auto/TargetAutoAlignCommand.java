package frc.robot.commands.swervedrive.auto;

import java.lang.annotation.Target;
import java.util.Optional;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.CurrentUnit;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import limelight.Limelight;
import limelight.networktables.LimelightData;
import limelight.results.RawFiducial;


public class TargetAutoAlignCommand extends Command {
    public SwerveSubsystem drivebase;
    Limelight limelight = new Limelight("limelight-butler");

    int[] validTags = {2};
    TargetAprilTags targetAprilTags = new TargetAprilTags(validTags);

    // == AUTO-ALIGNMENT CONFIGURATION ==

    final double targetDistanceInches = 25; // the distance from the tag in inches that the robot is trying to reach
    final double targetDistanceError = 0.05; // the amount of error that's allowed when trying to reach the target distance value
    final double driveRubberBandingMultiplier = 1; // robot drive speed when rubberbanding
    final double correctionSpeed = 0.5; // the speed for the robot to drive backwards if it gets too close to the april tags
    
    final double seekingRotationSpeed = 1.5; // the speed the robot rotates at when searching for april tags
    final double rotationalRubberBandingMultiplier = 0.12; // rotational speed when rubberbanding
    final double seekingError = 0.13; // the amount of error that's allowed when trying to align with the april tags
    
    // be careful with these ones
    final double driveSpeedLimit = 0.9; // used when rubberbanding driving (measured in m/s)
    final double rotationalSpeedLimit = 0.8; // used when rotationally rubberbanding, prevents the robot from going psycho (measured in degrees per second)
    





    // == DON'T TOUCH THESE ==

    // current stage of aligning, with 0 being rotating, and 1 being moving back and forth
    // when it equals 2, the robot completes alignment and stops moving
    int currentAligningStage;
    double lastRotation;
    double degreesRotated;



    public TargetAutoAlignCommand(SwerveSubsystem swerveSubsystem) {
        drivebase = swerveSubsystem;
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

    // returns the robot's rotation in degrees, adjusted to always be position instead of negative
    public double getAdjustedRotation(double rotation) {
        if (rotation < 0.0) {
            return rotation + 360.0;
        }

        return rotation;
    }

    public double getRubberBandingSpeed(double distanceFromCenter, double multiplier, double limit, double error) {
        if (distanceFromCenter > error || distanceFromCenter < error * -1) {
            return clampCalc(distanceFromCenter * multiplier * -1, limit * -1, limit);
        } else {
            return 0.0;
        }
    }

    @Override
    public void initialize() {
        currentAligningStage = 0;
        lastRotation = 0.0;
        degreesRotated = 0.0;
    }


    @Override
    public void execute() {
        if (currentAligningStage == 0) {
            RawFiducial[] validAprilTags = targetAprilTags.retrieveValidTags();

            if (validAprilTags.length > 0) {
                System.out.println("Robot out of alignment! Rotating...");
                System.out.println(getRubberBandingSpeed(validAprilTags[0].txnc, rotationalRubberBandingMultiplier, rotationalSpeedLimit, seekingError));
                drivebase.drive(new Translation2d(0, 0), getRubberBandingSpeed(validAprilTags[0].txnc, rotationalRubberBandingMultiplier, rotationalSpeedLimit, seekingError), false);

                // check if robot is aligned
                if (isApproximatelyEqual(validAprilTags[0].txnc, 0.0, seekingError)) {
                    drivebase.zeroGyro();
                    currentAligningStage = 1;
                }
            } else {
                degreesRotated += getAdjustedRotation(drivebase.getHeading().getDegrees()) - lastRotation;
                if (degreesRotated > 357.0) {
                    currentAligningStage = 2;
                } else {
                    lastRotation = getAdjustedRotation(drivebase.getHeading().getDegrees());
                    drivebase.drive(new Translation2d(0, 0), seekingRotationSpeed, false);
                }
                System.out.println(degreesRotated);
            }

        } else {
            RawFiducial[] validAprilTags = targetAprilTags.retrieveValidTags();

            if (validAprilTags.length > 0) {
                double tagDistance = targetAprilTags.getDistanceFromTagMeters(validAprilTags[0]);

                System.out.println(tagDistance);

                if (isApproximatelyEqual(tagDistance, targetDistanceInches, targetDistanceError)) {
                    currentAligningStage = 2;
                } else {
                    System.out.println("Robot out of alignment! Moving forwards/backwards...");
                    drivebase.drive(new Translation2d(getRubberBandingSpeed(tagDistance - targetDistanceInches, driveRubberBandingMultiplier, driveSpeedLimit, targetDistanceError), 0), 0.0, false);
                }
                
            } else {

            }
            
        }

    }

    @Override
    public boolean isFinished() {
        if (currentAligningStage == 2) {
            System.out.println("Aligning complete!");
            drivebase.lock();
            return true;
        }
        return false;
    }

@Override
public void end(boolean interrupted) {

}
}
