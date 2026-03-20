package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.Command;


public class ShooterCommand extends Command {
    SparkMax ammoGuide;
    SparkMax firingWheels;

    public ShooterCommand(SparkMax ammoGuide, SparkMax firingWheels) {
        this.ammoGuide = ammoGuide;
        this.firingWheels = firingWheels; 
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        ammoGuide.set(0.1);
        firingWheels.set(0.1);
    }

    @Override
    public void end(boolean interrupted){
        ammoGuide.set(0);
        firingWheels.set(0);
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
