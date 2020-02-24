package frc.robot.EAC;

public abstract class Actions extends EACBase {

  public abstract void runActions();

  public abstract void interruptActions();

  public void resetActions() {
  }

  public final void doInterruptActions() {
    interruptActions();
    resetActions();
  }

  protected Events getEvents(String name) {
    return RobotManager.getEvents(this, name);
  }

  protected Actions spawnActions(Class<? extends Actions> actions) {
    return RobotManager.spawnActions(this, actions);
  }
}