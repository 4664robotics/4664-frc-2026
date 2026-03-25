package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.Command;

public class IntakePushCommand extends Command {
    SparkMax intakePull;

    public IntakePushCommand(SparkMax intakePull) {
        this.intakePull = intakePull;
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        intakePull.set(0.85);
    }

    @Override
    public void end(boolean interrupted){
        intakePull.set(0);
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
