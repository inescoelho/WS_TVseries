package server.application;


import ontology.OntologyHandler;

public class Main {

    public static void main(String[] args) {
        testSearch();
    }

    public static void testSearch() {
        OntologyHandler ontologyHandler = new OntologyHandler("tv_series_ontology_current.rdf", "RDF/XML");

        ontologyHandler.performSearch("comedy and animation with johnny galecki and jim parsons");
        //ontologyHandler.performSearch("sci-fi orphan adventure action");
        //ontologyHandler.performSearch("series uma família muito moderna comedy");
        //ontologyHandler.performSearch("series uma família muito moderna actor jim parsons");
        //ontologyHandler.performSearch("series uma família muito moderna actor jim parsons");
        //ontologyHandler.performSearch("series big bang series uma família actor jim parsons");
        //ontologyHandler.performSearch("series comedy uma família muito moderna actor sofía vergara creator steven levitan");
        //ontologyHandler.performSearch("series romance foi assim que aconteceu series uma família");
        //ontologyHandler.performSearch("ze manel");
    }
}
