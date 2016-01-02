<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>

    <div class="series-header" id="seriesTitle"></div>

    <div class="row">

        <!-- left column -->
        <div class="col-md-4">
            <div class="text-center" id="seriesImage"></div>
        </div>

        <!-- right column -->
        <div class="col-md-8" id="rightInfo"></div>
    </div>

    <div class="spacer">&nbsp;</div>
    <div class="spacer">&nbsp;</div>
    <div class="container" id="creators">
        <p><b>Created by: </b></p>
    </div>

    <div class="spacer">&nbsp;</div>
    <div class="spacer">&nbsp;</div>
    <div class="container" id="actors">
        <p><b>Actors: </b></p>
    </div>

    <script>
        function getSeriesInfo() {

            var data = {"id" : "${requestScope.seriesID}"};

            $.ajax({
                type: "POST",
                url: "http://localhost:8090/getSeriesInfo",
                data: JSON.stringify(data),
                async: true,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.setRequestHeader("Accept", "application/json")
                },
                success: function (series) {
                    $('#seriesTitle').append("<h1 class=\"series-title\">" + series["title"] + "</h1>");

                    if (series["imageURL"] == "")
                        series["imageURL"] ="http://i.imgur.com/0zxp2G8.jpg";

                    $('#seriesImage').append("<img src=" + series["imageURL"] + "width=\"400\" height=\"300\">");
                    if (series["finishYear"] != "-1")
                        $('#rightInfo').append("<p>" + series["pilotYear"] + "-" + series["finishYear"] + "</p>");
                    else
                        $('#rightInfo').append("<p>" + series["pilotYear"] + "-</p>");
                    $('#rightInfo').append("<p><b>Storyline: </b>" + series["description"] + "</p>");
                    $('#rightInfo').append("<p><b>Duration: </b>" + series["episodeDuration"] + " min</p>");
                    $('#rightInfo').append("<p><b>Rating: </b>" + series["rating"] + "</p>");
                    $('#rightInfo').append("<p><b>Description: </b>" + series["storyline"] + "</p>");

                    for (var creator = 0; creator < series["creators"].length; creator ++)
                    {
                        if (series["creators"][creator][2] == "")
                            series["creators"][creator][2] ="http://i.imgur.com/0zxp2G8.jpg";

                        $('#creators').append(
                                "<div class=\"row\">" +
                                    "<div class=\"col-md-3 col-md-offset-2\">" +
                                        "<div class=\"text-center\" id=\"creatorImage\"" + creator + "\">" +
                                            "<a href=person?id=" + series["creators"][creator][0] + ">" +
                                                "<img src=" + series["creators"][creator][2] + "width=\"150\" height=\"100\">" +
                                            "</a>" +
                                        "</div>" +
                                    "</div>" +
                                    "<div class=\"col-md-7\">" +
                                        "<a href=person?id=" + series["creators"][creator][0] + ">" +
                                            "<p>" + series["creators"][creator][1] + "</p>" +
                                        "</a>" +
                                    "</div>" +
                                "</div>");
                    }

                    for (var actor = 0; actor < series["actors"].length; actor ++)
                    {
                        if (series["actors"][actor][2] == "")
                            series["actors"][actor][2] ="http://i.imgur.com/0zxp2G8.jpg";

                        $('#actors').append(
                                "<div class=\"row\">" +
                                    "<div class=\"col-md-3 col-md-offset-2\">" +
                                        "<div class=\"text-center\" id=\"actorsImage\"" + actor + "\">" +
                                            "<a href=person?id=" + series["actors"][actor][0] + ">" +
                                                "<img src=" + series["actors"][actor][2] + "width=\"150\" height=\"100\">" +
                                            "</a>" +
                                        "</div>" +
                                    "</div>" +
                                    "<div class=\"col-md-7\">" +
                                        "<a href=person?id=" + series["actors"][actor][0] + ">" +
                                            "<p>" + series["actors"][actor][1] + "</p>" +
                                        "</a>" +
                                    "</div>" +
                                "</div>");
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
            getSeriesInfo();
            performRecommendation();
        });
    </script>
</t:wrapper>