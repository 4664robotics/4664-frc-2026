package frc.robot.commands.swervedrive.auto;

import limelight.Limelight;
import limelight.networktables.LimelightPoseEstimator;
import limelight.results.RawFiducial;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;

public class TargetAprilTags {
    int[] validTags;

    Limelight limelight = new Limelight("limelight-butler");

    final double LIMELIGHT_MOUNT_ANGLE_DEGREES = 0;
    final double LIMELIGHT_LENS_HEIGHT_INCHES = 0;
    final double GOAL_HEIGHT_INCHES = 0;


    public TargetAprilTags(int[] validTags) {
        this.validTags = validTags;
    }

    // returns an array containing the valid tags listed in the valid tags array
    public RawFiducial[] retrieveValidTags() {
        ArrayList<RawFiducial> foundTags = new ArrayList<RawFiducial>();

        // zero limelight position and get data from scan
        limelight.getSettings().withCameraOffset(Pose3d.kZero);
        RawFiducial[] scanData = limelight.getData().getRawFiducials();

        for (int i = 0; i < scanData.length; i++) {
            for (int j = 0; j < validTags.length; j++) {
                if (scanData[i].id == validTags[i]) {
                    foundTags.add(scanData[i]);
                }
            }
        }

        RawFiducial[] foundTagsArray = {};

        for (int i = 0; i < foundTags.size(); i++) {
            foundTagsArray[i] = foundTags.get(i);
        }

        return foundTagsArray;
    }

    public double getDistanceFromTagMeters(RawFiducial tag) {
        double angleToGoalDegrees = LIMELIGHT_MOUNT_ANGLE_DEGREES + tag.tync;
        double angleToGoalRadians = angleToGoalDegrees * (Math.PI / 180);

        return (GOAL_HEIGHT_INCHES - LIMELIGHT_LENS_HEIGHT_INCHES) / Math.tan(angleToGoalRadians);
    }

    public Pose2d getCurrentPose() {
        return limelight.getLatestResults().get().getBotPose2d();
    }
}
