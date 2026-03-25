package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.Command;


public class ShooterAmmoGuideForward extends Command {
    SparkMax ammoGuide;
    

    public ShooterAmmoGuideForward(SparkMax ammoGuide) {
        this.ammoGuide = ammoGuide;
         
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        ammoGuide.set(-0.65);
        
    }

    @Override
    public void end(boolean interrupted){
        ammoGuide.set(0);
        
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
