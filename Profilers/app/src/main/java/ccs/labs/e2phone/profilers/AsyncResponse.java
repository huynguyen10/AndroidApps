package ccs.labs.e2phone.profilers;

/**
 * Created by huynguyen on 08.02.18.
 */

public interface AsyncResponse {
    void onTaskComplete(String message, boolean isSuccess);
}
