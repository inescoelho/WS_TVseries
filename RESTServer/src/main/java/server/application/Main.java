package server.application;


import ontology.OntologyHandler;

public class Main {

    public static void main(String[] args) {
        //testSearch();

        int finish = 2;
        int start = 0;
        int i;

        for (i=0; i< 100; i++) {
            int result = (int) (Math.random() * (finish - start) + start);
            System.out.println(result);
        }
    }

    public static void testSearch() {
        OntologyHandler ontologyHandler = new OntologyHandler("tv_series_ontology_current.rdf", "RDF/XML");

        ontologyHandler.performSearch("series comedy ended between 2012 2014");
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
