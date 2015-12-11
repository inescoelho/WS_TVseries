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

import java.util.ArrayList;

@RestController
public class Controller {

    private OntologyHandler ontologyHandler;

    public Controller() {
        ontologyHandler = new OntologyHandler("tv_series_ontology_current.rdf", "RDF/XML");
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

        return ontologyHandler.getSeriesInfo(inputData.getId());
    }

    @CrossOrigin
    @RequestMapping(value = "/getPersonInfo", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Person getPersonInfo(@RequestBody GetInfoInput inputData) {
        return ontologyHandler.getPersonInfo(inputData.getId());
    }

    @CrossOrigin
    @RequestMapping(value="/search", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody SearchResult performSearch(@RequestBody SearchInput searchInput) {
        System.out.println("Got query " + searchInput.getQuery());

        return ontologyHandler.performSearch(searchInput.getQuery());
    }
}
