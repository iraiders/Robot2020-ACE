package frc.robot.ACE.Controllers;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.List;

public class Controller {

  private GenericHID controller;

  private boolean[] buttonHeldState;
  private boolean[] buttonLastState;
  private boolean[] buttonToggleState;

  private static final int the_number_of_ports = 6;
  private static boolean[] ports = new boolean[the_number_of_ports];
  private boolean has_found_name = false;
  private static Integer controller_ids = 1;
  private Integer controller_id = -1;
  private boolean is_active = false;
  private boolean has_state = false;
  private String groupName;
  private String[] names;
  private String name;
  private int setPort = -1;
  private int port = -1;


  private Controller() {
    controller_id = controller_ids;
    controller_ids = controller_ids + 1;
  }

  public Controller(String name) {
    this();
    this.name = name;
  }

  public Controller(String name, int port) {
    this();
    this.name = name;
    this.setPort = port;
  }

  public Controller(String groupName, List<String> names) {
    this();
    this.groupName = groupName;
    this.names = new String[names.size()];
    this.names = names.toArray(this.names);
  }

  protected void setController(GenericHID controller) {
    this.controller = controller;
  }

  protected GenericHID getController() {
    return controller;
  }

  protected void setPort(int port) {
    this.port = port;
  }

  protected int getPort() {
    return port;
  }

  protected void setName(String name) {
    this.name = name;
  }

  protected String getName() {
    return name;
  }

  protected void setNames(String[] names) {
    this.names = names;
  }

  protected String[] getNames() {
    return names;
  }

  private String getControllerName(String baseName) {
    return  "Controller(" + controller_id.toString() + "): " + baseName;
  }

  private String getControllerGroupName(String groupName) {
    return  "Controller Group(" + controller_id.toString() + "): " + groupName;
  }

  private void scanReport(String report) {
    if (names != null) {
      SmartDashboard.putString(getControllerGroupName(groupName), report);
    } else {
      SmartDashboard.putString(getControllerName(name), report);
    }
  }

  private void scan(String name) {
    for (int i = 0; i < the_number_of_ports; i++) {
      Joystick test = new Joystick(i);
      if (name.equals(test.getName())) {
        has_found_name = true;
        if (!ports[i]) {
          initialize(name, i);
          ports[i] = true;
          setActive();
          setPort(i);
        }
      }
    }
  }

  private void scan(String[] names) {
    for (String name : names) {
      scan(name);
    }
  }

  private void scan(int setPort) {
    Joystick test = new Joystick(setPort);
    if (name.equals(test.getName())) {
      has_found_name = true;
      if (!ports[setPort]) {
        initialize(name, setPort);
        ports[setPort] = true;
        setActive();
        setPort(setPort);
      }
    }
  }

  private void scanPorts() {
    has_found_name = false;
    is_active = false;
    if (port != -1) {
      ports[port] = false;
      port = -1;
    }
    if (setPort != -1) {
      scan(setPort);
      if (!getIsActive()) {
        if (!has_found_name) {
          scanReport("Given port does not match name, port: " + setPort + ".");
        } else {
          scanReport("Port is already in use, port: " + setPort + "." );
        }
        has_state = false;
      } else{
        scanReport("Initialized, on given port: " + setPort + "." );
      }
      return;
    }
    if (names != null) {
      scan(names);
      if (!getIsActive()) {
        if (!has_found_name) {
          scanReport("No controller with a name in group.");
        } else {
          scanReport("No more ports are available for: " + name + "." );
        }
        has_state = false;
      } else {
        scanReport("Initialized, with: " + name + ".");
      }
    } else {
      scan(name);
      if (!getIsActive()) {
        if (!has_found_name) {
          scanReport("No controller with that name.");
        } else {
          scanReport("No more ports are available for: " + name + "." );
        }
        has_state = false;
      } else {
        scanReport("Initialized.");
      }
    }
  }

  public void resetState() {
    has_state = false;
  }

  public boolean getIsActive() {
    return is_active;
  }

  public void setActive() {
    is_active = true;
  }

  protected void generateCustomState() {

  }

  protected final void generateState() {
    if (has_state) return;
    int count = controller.getButtonCount() + 1;
    buttonHeldState = new boolean[count];
    buttonLastState = new boolean[count];
    buttonToggleState = new boolean[count];
    generateCustomState();
    has_state = true;
  }

  public void initialize(String name, int port) {
    controller = new Joystick(port);
    generateState();
    setName(name);
  }

  public void update() {
    scanPorts();
    updateButtons(controller, buttonHeldState, buttonLastState);
    updateButtonToggles(controller, buttonToggleState, buttonHeldState, buttonLastState);
  }

  protected void updateButtons(GenericHID controller, boolean[] buttonHeldState, boolean[] buttonLastState) {
    if (!getIsActive()) return;
    int count = buttonHeldState.length;
    for (int i = 1; i < count; i++) {
      buttonLastState[i] = buttonHeldState[i];
      buttonHeldState[i] = controller.getRawButton(i);
    }
  }

  protected void updateButtonToggles(GenericHID controller, boolean[] buttontoggleState, boolean[] buttonHeldState, boolean[] buttonLastState) {
    if (!getIsActive()) return;
    int count = buttontoggleState.length;
    for (int i = 1; i < count; i++) {
      if (getButtonPressed(i, buttonHeldState, buttonLastState) && !buttontoggleState[i]) {
        buttontoggleState[i] = true;
      } else if (getButtonPressed(i, buttonHeldState, buttonLastState) && buttontoggleState[i]) {
        buttontoggleState[i] = false;
      }
    }
  }

  protected boolean getButtonPressed(int button, boolean[] buttonHeldState, boolean[] buttonLastState) {
    if (!getIsActive()) return false;
    if (button >= buttonHeldState.length) return false;
    return buttonHeldState[button] && !buttonLastState[button];
  }

  protected boolean getButtonHeld(int button, boolean[] buttonHeldState) {
    if (!getIsActive()) return false;
    if (button >= buttonHeldState.length) return false;
    return buttonHeldState[button];
  }

  protected boolean getButtonToggle(int button, boolean[] buttontoggleState) {
    if (!getIsActive()) return false;
    if (button >= buttontoggleState.length) return false;
    return buttontoggleState[button];
  }

  /**
   * Rumbles a given controller for a specified time
   *
   * @param stick Controller to rumble
   * @param ms    Time in Milliseconds
   */
  private void rumbleController(GenericHID stick, double intensity, int ms) {
    if (!getIsActive()) return;
    rumbleController(stick, intensity, ms, GenericHID.RumbleType.kLeftRumble);
  }

  /**
   * Rumbles a given controller for a specified time
   * Left rumble is like an earthquake, right rumble is like a vibrating toothbrush
   *
   * @param stick      Controller to rumble
   * @param ms         Time in Milliseconds
   * @param rumbleType Type of rumble to use
   */
  private static void rumbleController(GenericHID stick, double intensity, int ms, GenericHID.RumbleType rumbleType) {
    if (ms > 0) {
      new Thread(() -> {
        _setRumble(stick, intensity, rumbleType);
        try {
          Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
        _setRumble(stick, 0, rumbleType);
      }).start();
    } else {
      _setRumble(stick, intensity, rumbleType);
    }
  }

  private static void _setRumble(GenericHID stick, double intensity, GenericHID.RumbleType rumbleType) {
    stick.setRumble(rumbleType, intensity);
  }

  public boolean getButton(int button) {
    return getButtonPressed(button, buttonHeldState, buttonLastState);
  }

  public boolean getButtonHeld(int button) {
    return getButtonHeld(button, buttonHeldState);
  }

  public double getXAxis() {
    if (!getIsActive()) return 0.0;
    return controller.getX();
  }

  public double getYAxis() {
    if (!getIsActive()) return 0.0;
    return controller.getY();
  }

  public void rumble(double intensity, int ms) {
    rumbleController(controller, intensity, ms);
  }
}
