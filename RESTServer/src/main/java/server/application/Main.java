package server.application;


import ontology.OntologyHandler;

public class Main {

    public static void main(String[] args) {
        testSearch();
    }

    public static void testSearch() {
        OntologyHandler ontologyHandler = new OntologyHandler("tv_series_ontology_current.rdf", "RDF/XML");

        //ontologyHandler.performSearch("uma família muito moderna comedy");
        //ontologyHandler.performSearch("sci-fi orphan adventure action");
        //ontologyHandler.performSearch("series uma família muito moderna comedy");
        //ontologyHandler.performSearch("series uma família muito moderna actor jim parsons");
        //ontologyHandler.performSearch("series uma família muito moderna actor jim parsons");
        ontologyHandler.performSearch("series big bang series uma família actor jim parsons");
        //ontologyHandler.performSearch("series uma família muito moderna creator creator chuck lorre");
        //ontologyHandler.performSearch("series romance foi assim que aconteceu series uma família");
        //ontologyHandler.performSearch("ze manel");
    }
}
