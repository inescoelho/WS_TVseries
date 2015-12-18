package server.application;

import ontology.OntologyHandler;
import org.springframework.web.bind.annotation.*;
import server.data.SearchInput;
import server.data.OperationResult;
import server.data.ontology.Genre;
import server.data.GetInfoInput;
import server.data.ontology.Person;
import server.data.ontology.Series;

import java.io.*;
import java.util.ArrayList;

@RestController
public class Controller {

    private OntologyHandler ontologyHandler;
    private ArrayList<String> lastChecked;

    private final String lastPeopleCheckedFile = "src\\main\\resources\\lastPeopleChecked.txt";
    private final String lastSeriesCheckedFile = "src\\main\\resources\\lastSeriesChecked.txt";

    private final int lastItemsChecked = 20;
    private final int recommendedSeries = 6;
    private final int recommendedPeople = 4;

    public Controller() {
        ontologyHandler = new OntologyHandler("tv_series_ontology_current.rdf", "RDF/XML");

        lastChecked = new ArrayList<>();
        loadPeople();
        loadSeries();
    }

    @CrossOrigin
    @RequestMapping(value = "/getGenres", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody ArrayList<Genre> getGenres() {
        return ontologyHandler.getGenres();
    }

    @CrossOrigin
    @RequestMapping(value = "/getSeriesInfo", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Series getSeriesInfo(@RequestBody GetInfoInput inputData) {
        addSeriesToChecked(inputData.getId());
        return ontologyHandler.getSeriesInfo(inputData.getId());
    }

    @CrossOrigin
    @RequestMapping(value = "/getPersonInfo", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Person getPersonInfo(@RequestBody GetInfoInput inputData) {
        addPersonToChecked(inputData.getId());
        return ontologyHandler.getPersonInfo(inputData.getId());
    }

    @CrossOrigin
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody OperationResult performSearch(@RequestBody SearchInput searchInput) {
        System.out.println("Got query " + searchInput.getQuery());

        return ontologyHandler.performSearch(searchInput.getQuery());
    }

    @CrossOrigin
    @RequestMapping(value = "/getRecommendation", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody OperationResult getRecommendation() {
        return ontologyHandler.performRecommendation(lastChecked, lastItemsChecked, recommendedSeries, recommendedPeople);
    }

    private void addSeriesToChecked(String id) {
        addToChecked(id, true, lastChecked);
    }

    private void addPersonToChecked(String id) {
        addToChecked(id, false, lastChecked);
    }

    private void addToChecked(String id, boolean series, ArrayList<String> buffer) {
        String path;

        if (series) {
            path = lastSeriesCheckedFile;
        } else {
            path = lastPeopleCheckedFile;
        }

        // Check if series already exist
        if (buffer.contains(id)) {
            buffer.remove(id);
        }

        buffer.add(id);
        // Save to file
        saveCheckedToFile(path, buffer, series);
    }

    private void saveCheckedToFile(String path, ArrayList<String> buffer, boolean series) {
        String write;
        try {
            File file = new File(path);

            if (!file.exists() && !file.createNewFile()) {
                System.out.println("Could not create the file");
                return ;
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            for (String current : buffer) {
                if (series && current.contains("nm")) {
                    continue;
                } else if (!series && current.contains("tt")) {
                    continue;
                }
                write = current + "\n";
                fileOutputStream.write(write.getBytes());
            }

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPeople() {
        loadChecked(false, lastChecked);
    }

    private void loadSeries() {
        loadChecked(true, lastChecked);
    }

    private void loadChecked(boolean series, ArrayList<String> buffer) {
        String line, path;

        if (series) {
            path = lastSeriesCheckedFile;
        } else {
            path = lastPeopleCheckedFile;
        }

        try {
            File file = new File(path);

            if (!file.exists()) {
                System.out.println("No previous history. Going to create file");
                if (!file.createNewFile()) {
                    System.out.println("Could not create new file");
                    return ;
                }
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while(true) {
                line = bufferedReader.readLine();

                if (line == null) {
                    break;
                }
                buffer.add(line);
            }

            bufferedReader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
