package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.Command;

public class ClimberUpCommand extends Command {

    SparkMax smg = new SparkMax(0, MotorType.kBrushless);

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        smg.set(0.1);
    }

    @Override
    public void end(boolean interrupted){
        smg.set(0);
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
