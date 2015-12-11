package server.application;

import ontology.OntologyHandler;
import org.springframework.web.bind.annotation.*;
import server.data.SearchInput;
import server.data.SearchResult;
import server.data.ontology.Genre;
import server.data.GetInfoInput;
import server.data.ontology.Person;
import server.data.ontology.Series;
import server.data.test.InputData;
import server.data.test.OutputData;

import java.io.*;
import java.util.ArrayList;

@RestController
public class Controller {

    private OntologyHandler ontologyHandler;
    private ArrayList<String> lastPeopleChecked;
    private ArrayList<String> lastSeriesChecked;

    private final String lastPeopleCheckedFile = "src\\main\\resources\\lastPeopleChecked.txt";
    private final String lastSeriesCheckedFile = "src\\main\\resources\\lastSeriesChecked.txt";

    public Controller() {
        ontologyHandler = new OntologyHandler("tv_series_ontology_current.rdf", "RDF/XML");

        loadPeople();
        loadSeries();
    }

    @CrossOrigin
    @RequestMapping(value = "/test", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody OutputData HelloWorld(@RequestBody InputData inputData) {
        System.out.println("Got message: " + inputData);

        return new OutputData(inputData.getMessage() + " Returned", inputData.getCode() + 1);
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
    @RequestMapping(value="/search", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody SearchResult performSearch(@RequestBody SearchInput searchInput) {
        System.out.println("Got query " + searchInput.getQuery());

        return ontologyHandler.performSearch(searchInput.getQuery());
    }

    private void addSeriesToChecked(String id) {
        addToChecked(id, lastSeriesCheckedFile, lastSeriesChecked);
    }

    private void addPersonToChecked(String id) {
        addToChecked(id, lastPeopleCheckedFile, lastPeopleChecked);
    }

    private void addToChecked(String id, String path, ArrayList<String> buffer) {
        // Check if series already exist
        if (buffer.contains(id)) {
            buffer.remove(id);
        }

        buffer.add(id);
        // Save to file
        saveCheckedToFile(path, buffer);
    }

    private void saveCheckedToFile(String path, ArrayList<String> buffer) {
        String write;
        try {
            File file = new File(path);

            if (!file.exists() && !file.createNewFile()) {
                System.out.println("Could not create the file");
                return ;
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            for (String current : buffer) {
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
        lastPeopleChecked = new ArrayList<>();
        loadChecked(lastPeopleCheckedFile, lastPeopleChecked);
    }

    private void loadSeries() {
        lastSeriesChecked = new ArrayList<>();
        loadChecked(lastSeriesCheckedFile, lastSeriesChecked);
    }

    private void loadChecked(String path, ArrayList<String> buffer) {
        String line;

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
