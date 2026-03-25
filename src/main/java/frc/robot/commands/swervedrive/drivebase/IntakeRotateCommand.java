package frc.robot.commands.swervedrive.drivebase;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

public class IntakeRotateCommand extends Command {
    SparkMax intakeSpin;
    DigitalInput limitSwitchBottom;
    DigitalInput limitSwitchTop;
    CommandJoystick joystick;

    final double SPEED_MULT = 0.28;

    public IntakeRotateCommand(SparkMax intakeSpin, DigitalInput limitSwitchBottom,DigitalInput limitSwitchTop, CommandJoystick joystick) {
        this.intakeSpin = intakeSpin;
        this.limitSwitchBottom = limitSwitchBottom;
        this.limitSwitchTop = limitSwitchTop;
        this.joystick = joystick;
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute(){
        double joystickInput = joystick.getRawAxis(1);

        //checks if joystick is being pulled back
        if (joystickInput > 0){
            if (limitSwitchTop.get()){
                intakeSpin.set(joystickInput * SPEED_MULT);
            }

        } else { //Checks if joystick is being pulled forward
            if (limitSwitchBottom.get()) {
                intakeSpin.set(joystickInput * SPEED_MULT);
            }
        }
        
    }

    @Override
    public void end(boolean interrupted){
        intakeSpin.stopMotor();
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
