package IMDBCrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Class that retrieves the tv shows names and movie id's from a 2-column .csv file (stored in the resources folder of
 * this project) and stores them in the "fileNameMap" HashMap
 */
public class FileLoader {

    private HashMap<String, String> fileNameMap;

    /**
     * Class constructor
     */
    public FileLoader() {
        fileNameMap = new HashMap<>();
    }

    /**
     * Load the the tv shows names and movie id's from the given source .csv file
     * @param filePath The source .csv file
     * @return A boolean value, signalling the success or failure of the operation
     */
    public boolean loadIDsFromFile(String filePath) {
        String line;
        String[] currentLine;

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL fileURL = classLoader.getResource(filePath);

            if (fileURL == null) {
                System.out.print("NO FILE");
                return false;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileURL.getFile()));

            while ( (line = bufferedReader.readLine()) != null ){
                currentLine = line.split(",");
                fileNameMap.put(currentLine[0], currentLine[1]);
                System.out.println(currentLine[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("ERRO");
            return false;
        }
        return true;
    }

    /**
     * Simple getter for the "fileNameMap" HashMap
     * @return The "fileNameMap" HashMap
     */
    public HashMap<String, String> getFileNameMap() {
        return fileNameMap;
    }

}