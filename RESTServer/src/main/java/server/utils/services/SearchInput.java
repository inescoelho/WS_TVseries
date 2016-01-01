package server.utils.services;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchInput {

    private String query;

    public SearchInput(@JsonProperty("query") String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
