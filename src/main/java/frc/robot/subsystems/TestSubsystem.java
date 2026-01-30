package frc.robot.subsystems;

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
            RawFiducial[] aprilTags = scanData.getRawFiducials(); // get list of april tags

            for (int i = 0; i < aprilTags.length; i++) {
                    RawFiducial aprilTag = aprilTags[i];
                    System.out.println("== Tag ID: " + aprilTag.id + " ==");

                    System.out.println("Tx: " + aprilTag.txnc);
                    System.out.println("Ty: " + aprilTag.tync);
                    System.out.println("Ta: " + aprilTag.ta);
            }
        }
    }

    //public void printLimelightLatency() {
    //    System.out.println("The Limelight has " + Double.toString())
    //} // note to self: look into getlatestresults()
}

// limelight.results.RawFiducial - for distance
// limelight.networktables.target.AprilTagFiducial - for angle