package server.application;


import ontology.OntologyHandler;

public class Main {

    public static void main(String[] args) {
        testSearch();
    }

    public static void testSearch() {
        OntologyHandler ontologyHandler = new OntologyHandler("tv_series_ontology_current.rdf", "RDF/XML");

        ontologyHandler.performSearch("series comedy with jim parsons born 1973");
        //ontologyHandler.performSearch("sci-fi orphan action");
        //ontologyHandler.performSearch("series uma família muito moderna comedy");
        //ontologyHandler.performSearch("series uma família muito moderna actor jim parsons");
        //ontologyHandler.performSearch("series big bang and uma família actor jim parsons");
        //ontologyHandler.performSearch("series comedy uma família muito moderna actor sofía vergara creator steven levitan");
        //ontologyHandler.performSearch("series comedy foi assim que aconteceu series uma família");
        //ontologyHandler.performSearch("moderna creator steven");
        //ontologyHandler.performSearch("series actor creator");
        //ontologyHandler.performSearch("ze manel");
    }
}
