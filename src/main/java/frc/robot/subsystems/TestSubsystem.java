package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import limelight.Limelight;
import limelight.networktables.LimelightData;
import limelight.networktables.LimelightTargetData;
import limelight.networktables.LimelightSettings.LEDMode;
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

    public void printAprilTag() {
        limelight.getSettings().withPipelineIndex(0);

        LimelightTargetData scanData = limelight.getData().targetData;
        try {
            System.out.println("AprilTag ID " + Integer.toString((int)scanData.getAprilTagID()) + "\nAprilTag %: " + Double.toString(scanData.getTargetArea()));
        } catch(Exception e) {
            System.out.println("No AprilTag detected!");
        }
    }

    //public void printLimelightLatency() {
    //    System.out.println("The Limelight has " + Double.toString())
    //} // note to self: look into getlatestresults()
}

// limelight.results.RawFiducial - for distance
// limelight.networktables.target.AprilTagFiducial - for angle