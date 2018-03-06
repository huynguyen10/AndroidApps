package ccs.labs.e2phone.profilers;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by huynguyen on 1/23/17.
 */

public class ShellCommand {
    // Execute "bash" command
    public static String execute(String command) {
        // Command with "su": su -c [cmd]
        // Example: su -c ls -l /proc/stat
        StringBuilder output = new StringBuilder();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine())!= null) {
                output.append(line).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
