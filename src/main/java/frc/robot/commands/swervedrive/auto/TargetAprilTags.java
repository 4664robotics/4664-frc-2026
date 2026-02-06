package frc.robot.commands.swervedrive.auto;

import limelight.results.RawFiducial;

public class TargetAprilTags {
    RawFiducial leftTag = null;
    RawFiducial rightTag = null;

    public TargetAprilTags() {
        
    }

    public RawFiducial getLeftTag() {
        return leftTag;
    }

    public void setLeftTag(RawFiducial leftTag) {
        this.leftTag = leftTag;
    }

    public RawFiducial getRightTag() {
        return rightTag;
    }

    public void setRightTag(RawFiducial rightTag) {
        this.rightTag = rightTag;
    }

    public boolean isValid() {
        return this.leftTag != null && this.rightTag != null;
    }

    public double getTagDistance() {
        return Math.sqrt(Math.pow(rightTag.txnc - leftTag.txnc, 2) + Math.pow(rightTag.tync - leftTag.tync, 2));
    }

    public double getTagMidpoint() {
        return (leftTag.txnc + rightTag.txnc) / 2;
    }
}
