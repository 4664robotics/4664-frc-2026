package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.Command;

public class IntakeSpinCounterclockwiseCommand extends Command {
    SparkMax intakeSpin;

    public IntakeSpinCounterclockwiseCommand(SparkMax intakeSpin) {
        this.intakeSpin = intakeSpin;
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        intakeSpin.set(0.1);
    }

    @Override
    public void end(boolean interrupted){
        intakeSpin.set(0);
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
