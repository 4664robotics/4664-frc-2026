package frc.robot.subsystems;

import java.util.Date;
import java.util.Optional;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import limelight.Limelight;
import limelight.networktables.LimelightData;
import limelight.networktables.LimelightResults;
import limelight.networktables.LimelightTargetData;
import limelight.networktables.LimelightSettings.LEDMode;
import limelight.networktables.target.AprilTagFiducial;
import limelight.results.RawFiducial;

public class TestSubsystem extends SubsystemBase {
    public boolean intArrayContainsValue(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }

        return false;
    }



    Limelight limelight = new Limelight("limelight-butler");
    CommandXboxController controller = new CommandXboxController(0);

    public void turnOnLight() {
        limelight.getSettings().withLimelightLEDMode(LEDMode.ForceOn);
    }

    public void turnOffLight() {
        limelight.getSettings().withLimelightLEDMode(LEDMode.ForceOff);
    }

    public void adjustLightBrightness() {
        if (controller.getRightY() < 0) { // more brightness

        } else if (controller.getRightY() > 0) { // less brightness

        }
    }

    public void printAprilTagData() {
        limelight.getSettings().withCameraOffset(Pose3d.kZero);
        LimelightData scanData = limelight.getData();
        
        try {
            System.out.println("Attempting to gather Limelight data!");
            //System.out.println(scanData);
        } catch(Exception e) {
            System.out.println(e);
        }
        
        if (scanData != null) {
            RawFiducial[] aprilTags = scanData.getRawFiducials(); // get array of april tags

            System.out.println("Timestamp: " + new Date());
            for (int i = 0; i < aprilTags.length; i++) {
                    RawFiducial aprilTag = aprilTags[i];
                    
                    System.out.println("== Tag ID: " + aprilTag.id + " ==");

                    System.out.println("Tx: " + aprilTag.txnc);
                    System.out.println("Ty: " + aprilTag.tync);
                    System.out.println("Ta: " + aprilTag.ta);
            }

            // CALCULATE DISTANCE THINGY
            if (aprilTags.length == 2) {
                int[] leftTagIds = {1};
                int[] rightTagIds = {2};
                RawFiducial leftTag = new RawFiducial(0, 0, 0, 0, 0, 0, 0);
                RawFiducial rightTag = new RawFiducial(0, 0, 0, 0, 0, 0, 0);

                for (int i = 0; i< 2; i++) {
                    if (intArrayContainsValue(leftTagIds, aprilTags[i].id)) {
                        leftTag = aprilTags[i];
                    }

                    if (intArrayContainsValue(rightTagIds, aprilTags[i].id)) {
                        rightTag = aprilTags[i];
                    }
                }

                double tagDistance = Math.sqrt(Math.pow(rightTag.txnc - leftTag.txnc, 2) + Math.pow(rightTag.tync - leftTag.tync, 2));
                System.out.println("Tag coordinate distance: " + tagDistance);
            }
        }
    }

    //public void printLimelightLatency() {
    //    System.out.println("The Limelight has " + Double.toString())
    //} // note to self: look into getlatestresults()
}

// limelight.results.RawFiducial - for distance
// limelight.networktables.target.AprilTagFiducial - for angle