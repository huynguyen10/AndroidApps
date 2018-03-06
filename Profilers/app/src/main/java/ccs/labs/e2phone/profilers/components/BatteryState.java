package ccs.labs.e2phone.profilers.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.Locale;

import ccs.labs.e2phone.profilers.ShellCommand;

import static ccs.labs.e2phone.profilers.ProfilerConstants.WIDTH;

public class BatteryState {
  private static final String TAG            = "BatteryState";
  private static final String CAPACITY_FILE  = "/sys/class/power_supply/battery/capacity";
  private static final String VOLTAGE_AVG    = "/sys/class/power_supply/battery/voltage_now";
  private static final String CURRENT_AVG    = "/sys/class/power_supply/battery/current_now";
  private static final String VOLTAGE_NOW    = "/sys/class/power_supply/battery/voltage_now";
  private static final String CURRENT_NOW    = "/sys/class/power_supply/battery/current_now";
  private static final String CHARGE_COUNTER = "/sys/class/power_supply/battery/charge_counter";
  private static final String OUTPUT_FORMAT  = "%"+WIDTH+"s,"+"%"+WIDTH+"s,"+"%"+WIDTH+"s,"+
                                               "%"+WIDTH+"s,"+"%"+WIDTH+"s,"+"%"+WIDTH+"s";

  private int            voltage;
  private BatteryManager bat;

  public BatteryState(Context context) {
    if (android.os.Build.VERSION.SDK_INT >= 21) {
      this.bat = (BatteryManager)context.getSystemService(Context.BATTERY_SERVICE);
    }

    IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
      }
    };
    context.registerReceiver(broadcastreceiver, intentfilter);
  }
  public String stringData() {
    if (android.os.Build.VERSION.SDK_INT >= 21 && bat != null) {
      int capacity = bat.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
      int current_now = bat.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
      int current_avg = bat.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
      int charge_counter = bat.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
      long energy_counter = bat.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);

      return String.format(Locale.US, OUTPUT_FORMAT,
              voltage, capacity, current_now, current_avg, charge_counter, energy_counter);
    }

//    String voltage = ShellCommand.execute("su -c cat " + VOLTAGE_AVG);
//    String capacity = ShellCommand.execute("su -c cat " + CHARGE_COUNTER);
//    String current_avg = ShellCommand.execute("su -c cat " + CURRENT_AVG);
//    String current_now = ShellCommand.execute("su -c cat " + CURRENT_NOW);
//    String charge_counter = ShellCommand.execute("su -c cat " + CHARGE_COUNTER);
    return String.format(Locale.US, OUTPUT_FORMAT,"","","","","","");
  }

  public String getLogHeader() {
    return String.format(Locale.US, OUTPUT_FORMAT,
              "voltage", "capacity", "current_now",
            "current_avg", "charge_counter", "energy_counter");
  }

}
