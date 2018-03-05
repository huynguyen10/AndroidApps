package ccs.labs.e2phone.profilers.components;

import java.io.File;
import java.util.Locale;

import ccs.labs.e2phone.profilers.LineReader;

import static ccs.labs.e2phone.profilers.ProfilerConstants.WIDTH;

public class BatteryState {
  private static final String TAG            = "BatteryState";
  private static final String CAPACITY_FILE  = "/sys/class/power_supply/battery/capacity";
  private static final String VOLTAGE_AVG    = "/sys/class/power_supply/battery/voltage_now";
  private static final String CURRENT_AVG    = "/sys/class/power_supply/battery/current_now";
  private static final String VOLTAGE_NOW    = "/sys/class/power_supply/battery/voltage_now";
  private static final String CURRENT_NOW    = "/sys/class/power_supply/battery/current_now";
  private static final String CHARGE_COUNTER = "/sys/class/power_supply/battery/charge_counter";
  private static final String OUTPUT_FORMAT  = "%"+WIDTH+"s,%"+WIDTH+"s,%"+WIDTH+"s";

  private String VOLTAGE_FILE;
  private String CURRENT_FILE;

  public BatteryState() {
    if (new File(VOLTAGE_AVG).exists()) {
      VOLTAGE_FILE = VOLTAGE_AVG;
    } else {
      VOLTAGE_FILE = VOLTAGE_NOW;
    }

    if (new File(CURRENT_AVG).exists()) {
      CURRENT_FILE = CURRENT_AVG;
    } else {
      CURRENT_FILE = CURRENT_NOW;
    }
  }
  public String stringData() {
    String capacity = LineReader.getSingleLine(CAPACITY_FILE, "r");
    String voltage = LineReader.getSingleLine(VOLTAGE_FILE, "r");
    String current = LineReader.getSingleLine(CURRENT_FILE, "r");

    return String.format(Locale.US, OUTPUT_FORMAT, capacity, voltage, current);
  }

  public String getLogHeader() {
    return String.format(Locale.US, OUTPUT_FORMAT,
              "capacity", "voltage", "current");
  }
}
