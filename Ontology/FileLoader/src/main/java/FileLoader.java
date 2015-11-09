import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class that retrieves the tv shows names and movie id's from a 2-column .csv file (stored in the resources folder of
 * this project) and stores them in the "fileNameList" ArrayList<String[]>
 */
public class FileLoader {

    private ArrayList<String[]> fileNameList;

    /**
     * Class constructor
     */
    public FileLoader() {
        fileNameList = new ArrayList<>();
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
                return false;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileURL.getFile()));

            while ( (line = bufferedReader.readLine()) != null ){
                currentLine = line.split(",");
                fileNameList.add(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Simple getter for the "fileNameList" ArrayList
     * @return The "fileNameList" ArrayList
     */
    public ArrayList<String[]> getFileNameList() {
        return fileNameList;
    }

}
