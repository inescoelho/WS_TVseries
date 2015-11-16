package utils;

import java.io.*;

/**
 * Custom Logger class, to enable us to get debug prints on the code
 */
public class MyLogger {

    // Writes to GLASSFISH_HOME/glassfish\domains\domain1\config\CustomLog.log
    private static final String logFileLocation = "CustomLog.log";

    public static void writeToLogFile(String message) {
        try {

            File file =new File(logFileLocation);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message + "\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
