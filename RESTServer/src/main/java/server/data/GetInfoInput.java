package server.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent the input data passed in the "getSeriesInfo" and "getPersonInfo" requests.
 * Contains a single attribute, containing the id of either the series or person from which the relevant information
 * will be retrieved
 */
public class GetInfoInput {

    private String seriesId;

    public GetInfoInput(@JsonProperty("seriesId") String seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }
}
