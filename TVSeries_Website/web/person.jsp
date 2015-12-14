<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>

    <div class="series-header" id="personName"></div>

    <div class="row">

        <!-- left column -->
        <div class="col-md-4">
            <div class="text-center" id="personImage"></div>
        </div>

        <!-- right column -->
        <div class="col-md-8" id="rightInfo"></div>
    </div>

    <div class="container" id="seriesInfo"></div>

    <script>
        function getPersonInfo() {

            var data = {"id" : "${requestScope.personID}"};

            $.ajax({
                type: "POST",
                url: "http://localhost:8090/getPersonInfo",
                data: JSON.stringify(data),
                async: true,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.setRequestHeader("Accept", "application/json")
                },
                success: function (person) {
                    $('#personName').append("<h1 class=\"actor-title\">" + person["name"] + "</h1>");
                    $('#personImage').append("<img src=" + person["imageURL"] + "width=\"300\" height=\"240\">");
                    if(person["birthDate"] != "")
                        $('#rightInfo').append("<p><b>Birthday: </b>" + person["birthDate"] + "</p>");
                    if(person["biography"] != "")
                        $('#rightInfo').append("<p><b>Biography: </b>" + person["biography"] + "</p>");
                    if (person["wikiURL"] != "")
                        $('#rightInfo').append("<p><a href=" + person["wikiURL"] + ">More info</a></p>");

                    console.log("Person basic data --> {id: " + person["id"] + ", name: " + person["name"] +
                    ", biography: " + person["biography"] + ", birthDate: " + person["birthDate"] +
                    ", wikiURL: " + person["wikiURL"] + ", totalSeriesAppearances: " +
                    person["seriesActed"].length + ", totalSeriesCreated: " + person["seriesCreated"] +
                    ", imageURL: " + person["imageURL"] + "}");

                    var firstSeriesActed = person["seriesActed"][0];
                    var firstSeriesCreated = person["seriesCreated"][0];

                    if (firstSeriesActed) {
                        console.log("\n\nFirst series acted in the list: {id: " + firstSeriesActed[0] + ", name: " +
                                firstSeriesActed[1] + ", imageURL: " + firstSeriesActed[2]);
                    }
                    if (firstSeriesCreated) {
                        console.log("\n\nFirst series created in the list: {id: " + firstSeriesCreated[0] + ", name: " +
                                firstSeriesCreated[1] + ", imageURL: " + firstSeriesCreated[2]);
                    }

                    if (person["seriesCreated"].length != 0)
                    {
                        $('#seriesInfo').append("<p><b>Creator of: </b></p>");
                        for (var creator = 0; creator < person["seriesCreated"].length; creator ++)
                        {
                            $('#seriesInfo').append(
                                    "<div class=\"row\">" +
                                        "<div class=\"col-md-3 col-md-offset-2\">" +
                                            "<div class=\"text-center\" id=\"creatorImage\"" + creator + "\">" +
                                                "<a href=series?id=" + person["seriesCreated"][creator][0] + ">" +
                                                    "<img src=" + person["seriesCreated"][creator][2] + "width=\"150\" height=\"100\">" +
                                                "</a>" +
                                            "</div>" +
                                        "</div>" +
                                        "<div class=\"col-md-7\">" +
                                            "<a href=series?id=" + person["seriesCreated"][creator][0] + ">" +
                                                "<p>" + person["seriesCreated"][creator][1] + "</p>" +
                                            "</a>" +
                                        "</div>" +
                                    "</div>");
                        }
                    }

                    if (person["seriesActed"].length != 0)
                    {
                        $('#seriesInfo').append("<p><b>Actor in: </b></p>");
                        for (var actor = 0; actor < person["seriesActed"].length; actor ++)
                        {
                            $('#seriesInfo').append(
                                    "<div class=\"row\">" +
                                        "<div class=\"col-md-3 col-md-offset-2\">" +
                                            "<div class=\"text-center\" id=\"actorImage\"" + actor + "\">" +
                                                "<a href=series?id=" + person["seriesActed"][actor][0] + ">" +
                                                     "<img src=" + person["seriesActed"][actor][2] + "width=\"150\" height=\"100\">" +
                                                "</a>" +
                                            "</div>" +
                                        "</div>" +
                                        "<div class=\"col-md-7\">" +
                                            "<a href=series?id=" + person["seriesActed"][actor][0] + ">" +
                                                "<p>" + person["seriesActed"][actor][1] + "</p>" +
                                            "</a>" +
                                        "</div>" +
                                    "</div>");
                        }
                    }
                },
                error: function (jqXHR, exception) {
                    console.log(jqXHR.status);
                    console.log(exception);
                }
            });
        }

        $(document).ready(function() {
            getGenres();
            getPersonInfo();
        });
    </script>
</t:wrapper>