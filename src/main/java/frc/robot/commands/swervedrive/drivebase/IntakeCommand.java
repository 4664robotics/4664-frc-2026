package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.Command;

public class IntakeCommand extends Command {
    SparkMax intake;

    public IntakeCommand(SparkMax intake) {
        this.intake = intake;
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        intake.set(0.1);
    }

    @Override
    public void end(boolean interrupted){
        intake.set(0);
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
