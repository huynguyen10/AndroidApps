package ccs.labs.e2phone.profilers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;


public class MainActivity extends Activity {
  private static final String TAG = "MainActivity";
  private static final int REQUEST_CODE_PERMISSION = 100;
  private static SharedPreferences prefs;
  private Intent mIntent;

  private EditText etDelay;
  private EditText etServer;
  private EditText etPort;

  // Control LogService
  private static boolean isServiceStarted = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    mIntent = new Intent(this, LogService.class);

    if (android.os.Build.VERSION.SDK_INT >= 23) {
      ActivityCompat.requestPermissions(this, new String[]{
              Manifest.permission.WRITE_EXTERNAL_STORAGE,
              Manifest.permission.READ_EXTERNAL_STORAGE,
              Manifest.permission.BATTERY_STATS},
              REQUEST_CODE_PERMISSION);
    }

    etDelay = findViewById(R.id.etFreq);
    etServer = findViewById(R.id.etServer);
    etPort = findViewById(R.id.etPort);

    Button btnStart = findViewById(R.id.btnStart);
    Button btnStop = findViewById(R.id.btnStop);

    btnStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isServiceStarted) {
          return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("samplingFreq", Integer.parseInt(etDelay.getText().toString()));
        editor.putString("ipAddr", etServer.getText().toString());
        editor.putInt("port", Integer.parseInt(etPort.getText().toString()));
        editor.commit();

        mIntent.putExtra("component",
                ProfilerConstants.COMP_BAT | ProfilerConstants.COMP_LCD);
        startService(mIntent);
        isServiceStarted = true;
      }
    });

    btnStop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!isServiceStarted) {
          return;
        }
        stopService(mIntent);
        isServiceStarted = false;
      }
    });
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    SharedPreferences.Editor editor = prefs.edit();

    switch (requestCode) {
      case REQUEST_CODE_PERMISSION:
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          editor.putBoolean("permission", true);
        } else {
          editor.putBoolean("permission", false);
        }
    }
    editor.commit();
  }
}
