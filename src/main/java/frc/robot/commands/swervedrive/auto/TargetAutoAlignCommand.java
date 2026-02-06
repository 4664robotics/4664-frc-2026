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

    public double clampCalc(double value, double lowEnd, double highEnd) {
        if (value < lowEnd) {
            return lowEnd;
        } else if (value > highEnd) {
            return highEnd;
        } else {
            return value;
        }
    }

    public boolean isValidAprilTags(RawFiducial[] aprilTags) {
        if (aprilTags.length == 2) {
            boolean leftTagValid = false;
            boolean rightTagValid = false;

            for (int i = 0; i < 2; i++) {
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

    public TargetAprilTags retrieveValidTargetTags(RawFiducial[] aprilTags) {
        if (aprilTags.length != 2) {
            return null;
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
    if (distanceFromCenter > 0.2 || distanceFromCenter < -0.2) {

        return clampCalc(distanceFromCenter * -0.15, -0.35, 0.35);
    } else {
        return 0.0;
    }
}


           /*  // CALCULATE DISTANCE THINGY
            if (aprilTags.length == 2) {

                RawFiducial leftTag = new RawFiducial(0, 0, 0, 0, 0, 0, 0);
                RawFiducial rightTag = new RawFiducial(0, 0, 0, 0, 0, 0, 0);



                double tagDistance = Math.sqrt(Math.pow(rightTag.txnc - leftTag.txnc, 2) + Math.pow(rightTag.tync - leftTag.tync, 2));
                System.out.println("Tag coordinate distance: " + tagDistance);
            }*/


@Override
public void initialize() {

}

@Override
public void execute() {
    RawFiducial[] aprilTags = getAprilTags().get();
    if (isValidAprilTags(aprilTags)) {
        drivebase.zeroGyro();
        double middle = (aprilTags[1].txnc + aprilTags[0].txnc) / 2;

        drivebase.drive(new Translation2d(0, 0), getRubberBandingSpeed(middle), true);
    } else {
        drivebase.drive(new Translation2d(0, 0), 1, true);
    }
}

@Override
public boolean isFinished() {
    return false;
}

@Override
public void end(boolean interrupted) {

}
}
