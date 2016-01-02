package server.application;


import ontology.OntologyHandler;

public class Main {

    public static void main(String[] args) {
        OntologyHandler ontologyHandler = new OntologyHandler("tv_series_ontology_current.rdf", "RDF/XML");
    }
}
