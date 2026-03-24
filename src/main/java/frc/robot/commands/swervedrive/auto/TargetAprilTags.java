package frc.robot.commands.swervedrive.auto;

import limelight.Limelight;
import limelight.networktables.LimelightPoseEstimator;
import limelight.networktables.PoseEstimate;
import limelight.results.RawFiducial;

import java.util.ArrayList;
import java.util.Optional;

import edu.wpi.first.math.geometry.Pose2d;

public class TargetAprilTags {
    int[] validTags;

    Limelight limelight = new Limelight("limelight-butler");

    final double LIMELIGHT_MOUNT_ANGLE_DEGREES = 0; // TODO: set actual camera mount angle
    final double LIMELIGHT_LENS_HEIGHT_INCHES = 0;  // TODO: set actual camera height
    final double GOAL_HEIGHT_INCHES = 0;             // TODO: set actual target height


    public TargetAprilTags() {
        validTags = new int[0];
    }

    public TargetAprilTags(int[] validTags) {
        this.validTags = validTags;
    }

    // Returns all visible tags when validTags is empty, otherwise filters to only the specified IDs.
    public RawFiducial[] retrieveValidTags() {
        RawFiducial[] scanData = limelight.getData().getRawFiducials();

        // No filter — return everything the limelight sees
        if (validTags.length == 0) {
            return scanData;
        }

        ArrayList<RawFiducial> foundTags = new ArrayList<RawFiducial>();

        for (int i = 0; i < scanData.length; i++) {
            for (int j = 0; j < validTags.length; j++) {
                if (scanData[i].id == validTags[j]) { // fix: was validTags[i]
                    foundTags.add(scanData[i]);
                }
            }
        }

        return foundTags.toArray(new RawFiducial[0]); // fix: was empty array assigned by index
    }

    public double getDistanceFromTagMeters(RawFiducial tag) {
        double angleToGoalDegrees = LIMELIGHT_MOUNT_ANGLE_DEGREES + tag.tync;
        double angleToGoalRadians = angleToGoalDegrees * (Math.PI / 180);

        if (angleToGoalRadians == 0) return 0; // fix: prevent 0/0 = NaN when constants are unset

        return (GOAL_HEIGHT_INCHES - LIMELIGHT_LENS_HEIGHT_INCHES) / Math.tan(angleToGoalRadians);
    }

    // Returns empty if Limelight has no valid pose estimate.
    public Optional<Pose2d> getCurrentPoseEstimateFromVision() {
        LimelightPoseEstimator poseEstimator = new LimelightPoseEstimator(limelight, LimelightPoseEstimator.EstimationMode.MEGATAG2);
        Optional<PoseEstimate> poseEstimate = poseEstimator.getPoseEstimate();
        if (poseEstimate.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(poseEstimate.get().pose.toPose2d());
    }
}
