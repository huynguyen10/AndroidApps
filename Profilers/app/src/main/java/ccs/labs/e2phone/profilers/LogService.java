package ccs.labs.e2phone.profilers;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;

import ccs.labs.e2phone.profilers.components.*;

import static ccs.labs.e2phone.profilers.ProfilerConstants.WIDTH;

public class LogService extends Service implements AsyncResponse {
  private static final String TAG = "LogService";
  private static final String OUTPUT_FORMAT = "%"+WIDTH+"s";

  // Log file
  private boolean     permission;
  private PrintWriter writer;
  private String      logData = "";
  private File        sourceFile;

  private int freq;
  private SharedPreferences prefs;

  // Logging process
  private Handler handler;
  private boolean stopLogging;

  // Components
  private BatteryState bat = null;
  private LCD lcd = null;


  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // TODO Auto-generated method stub
    super.onStartCommand(intent, flags, startId);

    if (ProfilerConstants.DEBUG) Log.d(TAG, "LogService started");

    // Identify the info to be logged
    int component = intent.getIntExtra("component", ProfilerConstants.COMP_BAT);

    if (ProfilerConstants.DEBUG) Log.d(TAG, "component = " + component);
    freq = intent.getIntExtra("samplingFreq", 10);

    prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    permission = prefs.getBoolean("permission", true);

    if ((component & ProfilerConstants.COMP_BAT) != 0) {
      bat = new BatteryState(getApplicationContext());
    }

    if ((component & ProfilerConstants.COMP_LCD) != 0) {
      lcd = new LCD(getApplicationContext());
    }

    if (permission) {
      try {
        sourceFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "LogData" + System.currentTimeMillis() + ".log");
        writer = new PrintWriter(sourceFile);
        String header = String.format(Locale.US, OUTPUT_FORMAT, "time");

        if (bat != null) {
          header += "," + bat.getLogHeader();
        }

        if (lcd != null) {
          header += "," + lcd.getLogHeader();
        }
        writer.println(header);

        // Start logging
        stopLogging = false;
        handler = new Handler();
        handler.postDelayed(runnable, 1000/freq);

      } catch (java.io.IOException e) {
        e.printStackTrace();
      }
    }
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();

    if (writer != null) {
      writer.close();
    }
    stopLogging = true;

    new Thread(new Runnable() {
      @Override
      public void run() {
        if (permission) {
          File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
          File[] files = dir.listFiles();
          for (File file : files) {
            if (file.getName().startsWith("LogData")) {
              UploadClient uploadClient = new UploadClient(file, LogService.this, prefs);
              uploadClient.start();

              try {
                uploadClient.join();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
        }
      }
    }).start();

    if (ProfilerConstants.DEBUG) Log.d(TAG, "LogService destroyed");
  }

  @Override
  public void onTaskComplete(String message, boolean isSuccess) {
    if (ProfilerConstants.DEBUG) Log.d(TAG, message);
  }

  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      if (!stopLogging) {
        handler.postDelayed(this, 1000/freq);
        dataLogging();
      }
    }
  };

  // Given a logging file name
  private void dataLogging() {
    // Time
    logData += String.format(Locale.US, OUTPUT_FORMAT, System.currentTimeMillis());

    // Logging data
    if (bat != null) {
      logData += "," + bat.stringData();
    }
    if (lcd != null) {
      logData += "," + lcd.stringData();
    }

    // Write the entire data to log file
    writer.println(logData);
    logData = "";
  }
}
