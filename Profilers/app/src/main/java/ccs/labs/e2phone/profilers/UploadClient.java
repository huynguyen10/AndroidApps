package ccs.labs.e2phone.profilers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadClient extends Thread {
  private static String TAG = "UploadClient";

  private File  sourceFile;
  private AsyncResponse callback = null;
  private SharedPreferences prefs;

  UploadClient(File sourceFile, AsyncResponse cb, SharedPreferences prefs) {
    this.sourceFile = sourceFile;
    this.callback = cb;
    this.prefs = prefs;
  }

  @Override
  public void run() {
    String response;
    boolean isSuccess = false;

    if (!sourceFile.isFile()) {
      response = "Source file not exist" + sourceFile;
      if (ProfilerConstants.DEBUG) Log.d(TAG, response);
    } else {
      String boundary = "----*****";
      String twoHyphens = "--";
      String lineEnd = "\r\n";

      try {
        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        String serverAddress = prefs.getString("ipAddr", ProfilerConstants.SERVER_ADDRESS);
        int serverPort = prefs.getInt("port", ProfilerConstants.SERVER_PORT);

        String serverAddr = ProfilerConstants.PROTOCOL +
                serverAddress + ":" +
                serverPort +
                ProfilerConstants.SERVER_DIR +
                ProfilerConstants.UPLOAD_SCRIPT;

        if (ProfilerConstants.DEBUG) Log.d(TAG, serverAddr);

        //Setup the request
        HttpURLConnection httpURLConnection;
        URL url = new URL(serverAddr);

        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoInput(true);     // Allow Inputs
        httpURLConnection.setDoOutput(true);    // Allow Outputs
        httpURLConnection.setUseCaches(false);  // Don't use a cached copy
//          if (connectionType == 2) {
//            // MOBILE
//            httpURLConnection.setConnectTimeout(60000);
//          }

        // This will help you to chunk your data into specific size,
        // so that you need not keep your entire file in the memory
        httpURLConnection.setChunkedStreamingMode(1024);

        httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Connection", "Keep-alive");
        httpURLConnection.setRequestProperty("Connection", "close");
        httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;" +
                " boundary=" + boundary);
        httpURLConnection.setRequestProperty("uploaded_file", sourceFile.toString());

        // Post data to server
        DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        outputStream.writeBytes(twoHyphens + boundary + lineEnd);

        outputStream.writeBytes("Content-Disposition: form-data; " +
                "name=\"uploaded_file\"; filename=\"" + sourceFile + "\"" + lineEnd);
        outputStream.writeBytes(lineEnd);


        // Create a buffer of maximum size
        int bytesAvailable = fileInputStream.available();
        int maxBufferSize = 1024 * 1024;
//        int maxBufferSize = 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // Read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
          outputStream.write(buffer, 0, bufferSize);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        // Send multipart form data necessary after file data...
        outputStream.writeBytes(lineEnd);
        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        // Responses from the server (code and message)
        int serverResponseCode = httpURLConnection.getResponseCode();
        String serverResponseMessage = httpURLConnection.getResponseMessage();

        if (ProfilerConstants.DEBUG) Log.d(TAG, "HTTP response: " +
                serverResponseCode + "-" + serverResponseMessage);

        if(serverResponseCode == 200){
          response = "File upload completed";
          isSuccess = true;
          sourceFile.delete();
        } else {
          response = serverResponseMessage;
        }

        // Close the streams
        fileInputStream.close();
        outputStream.flush();
        outputStream.close();
      } catch (MalformedURLException e) {
        response = "MalformedURLException: " + e.toString();
      } catch (IOException e) {
        response = "IOException: " + e.toString();
      }
    }
    if (callback != null) callback.onTaskComplete(response, isSuccess);
  }
}
