package ccs.labs.e2phone.profilers.components;

import java.util.Locale;

import ccs.labs.e2phone.profilers.LineReader;

import static ccs.labs.e2phone.profilers.ProfilerConstants.WIDTH;

/**
 * Created by huynguyen on 12/8/16.
 */

public class LCD {
  private static final String TAG = "LCD";
  private static final String BRIGHTNESS_FILE = "/sys/class/leds/lcd-backlight/brightness";

  public String stringData() {
    String brightness;

    if ((brightness = LineReader.getSingleLine(BRIGHTNESS_FILE, "r")).equals("")) {
      brightness = "0";
    }
    return String.format(Locale.US, "%"+WIDTH+"s", brightness);
  }

  public String getLogHeader() {
    return String.format(Locale.US, "%"+WIDTH+"s", "brightness");
  }
}
