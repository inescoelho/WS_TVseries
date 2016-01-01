package server.services;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent the input data passed in the "getSeriesInfo" and "getPersonInfo" requests.
 * Contains a single attribute, containing the id of either the series or person from which the relevant information
 * will be retrieved
 */
public class GetInfoInput {

    private String id;

    public GetInfoInput(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
