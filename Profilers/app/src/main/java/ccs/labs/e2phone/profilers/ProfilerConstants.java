package ccs.labs.e2phone.profilers;

public class ProfilerConstants {
  public static final boolean DEBUG = true;   // Debug enable/disable
  public static final String  WIDTH = "20";

  static final int COMP_BAT    = 0x0001;  // Power Consumption
  static final int COMP_LCD    = 0x0002;
  static final int COMP_CPU    = 0x0004;
  static final int COMP_WIFI   = 0x0008;
  static final int COMP_3G     = 0x0010;

  static final String PROTOCOL = "http://";
  static final String SERVER_ADDRESS = "xxx.xxx.xxx.xxx";
  static final int    SERVER_PORT = 8080;
  static final String SERVER_DIR = "/uploads/";
  static final String UPLOAD_SCRIPT = "UploadToServer.php";
}
