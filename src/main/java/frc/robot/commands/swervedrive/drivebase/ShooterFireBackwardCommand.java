package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.Command;


public class ShooterFireBackwardCommand extends Command {
    
    SparkMax firingWheels;

    public ShooterFireBackwardCommand(SparkMax firingWheels) {
        
        this.firingWheels = firingWheels; 
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        firingWheels.set(-1);
    }

    @Override
    public void end(boolean interrupted){
        firingWheels.set(0);
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
