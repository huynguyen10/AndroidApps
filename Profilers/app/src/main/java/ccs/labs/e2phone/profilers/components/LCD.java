package ccs.labs.e2phone.profilers.components;

import android.content.Context;
import android.provider.Settings;

import java.util.Locale;

import ccs.labs.e2phone.profilers.ShellCommand;

import static ccs.labs.e2phone.profilers.ProfilerConstants.WIDTH;

/**
 * Created by huynguyen on 12/8/16.
 */

public class LCD {
  private static final String TAG = "LCD";
  private static final String BRIGHTNESS_FILE = "/sys/class/leds/lcd-backlight/brightness";
  private static final String  OUTPUT_FORMAT = "%"+WIDTH+"s";

  private Context context;
  public LCD(Context context) {
    this.context = context;
  }
  public String stringData() {
    String brightness = "";

    try {
      brightness = String.format(Locale.US, OUTPUT_FORMAT,
              Settings.System.getInt(context.getContentResolver(),
                      Settings.System.SCREEN_BRIGHTNESS));
    } catch (Settings.SettingNotFoundException e) {
      e.printStackTrace();
    }

//    brightness = ShellCommand.execute("su -c cat " + BRIGHTNESS_FILE);

    return String.format(Locale.US, OUTPUT_FORMAT, brightness);
  }

  public String getLogHeader() {
    return String.format(Locale.US, OUTPUT_FORMAT, "brightness");
  }
}
