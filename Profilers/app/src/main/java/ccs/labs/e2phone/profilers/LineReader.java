package ccs.labs.e2phone.profilers;

import java.io.File;
import java.io.RandomAccessFile;

public class LineReader {
    public static String getSingleLine(String fileName, String mode) {
        if (!new File(fileName).exists()) {
            return "";
        }

        String text;
        try {
            RandomAccessFile reader = new RandomAccessFile(fileName, mode);
            text = reader.readLine();
            reader.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            text = "";
        }
        return text;
    }

    public static String[] getMultiLines(String fileName, String mode, int nLines) {
        String[] lines = new String[nLines];

        if (!new File(fileName).exists()) {
            return lines;
        }

        try {
            RandomAccessFile reader = new RandomAccessFile(fileName, mode);
            for (int i = 0; i < nLines; i++) {
                lines[i] = reader.readLine();
            }
            reader.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }

        return lines;
    }
}
