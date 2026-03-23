package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;

public class IntakeSpinClockwiseCommand extends Command {
    SparkMax intakeSpin;
    DigitalInput limitSwitch;

    public IntakeSpinClockwiseCommand(SparkMax intakeSpin, DigitalInput limitSwitch) {
        this.intakeSpin = intakeSpin;
        this.limitSwitch = limitSwitch;
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        if (!limitSwitch.get()) {
            intakeSpin.set(0.1);
        }
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
