package server.application;


import ontology.OntologyHandler;

import java.util.ArrayList;

public class Main {

    public static class Teste{
        ArrayList<String[]> a;

        Teste() {
            a = new ArrayList<>();
            a.add(new String[] {"ola", "adeus"});
        }
    }

    public static void main(String[] args) {
        //testSearch();
        Teste temp = new Teste();
        System.out.println("Before " + temp.a);
        test(temp);
        System.out.println("AFTER " + temp.a.contains(new String[] {"ola", "adeus"}));
    }

    public static void test(Teste temp) {
        temp.a.add(new String[] {"ola", "adeus"});
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
