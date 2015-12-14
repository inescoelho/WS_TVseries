<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>

    <div id="series">
    </div>

    <div id="people">
    </div>


    <script>
        function getSearchResult() {
            var data = {"query" : "${requestScope.searchQuery}"};

            $.ajax({
                type: "POST",
                url: "http://localhost:8090/search",
                data: JSON.stringify(data),
                async: true,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.setRequestHeader("Accept", "application/json")
                },
                success: function (data) {
                    console.log("Series Found: " + data["series"].length);
                    console.log("People Found: " + data["people"].length);

                    if (data["series"].length > 0) {
                        $('#series').append("<h1>Series</h1>");

                        for (var i = 0; i < data["series"].length; i++) {
                            var currentSeries = data["series"][i];

                            $('#series').append(
                                    "<div class=\"row\">" +
                                        "<div class=\"col-md-3 col-md-offset-2\">" +
                                            "<div class=\"text-center\" id=\"seriesImage\"" + currentSeries + "\">" +
                                                "<a href=series?id=" + currentSeries[1] + ">" +
                                                    "<img src=" + currentSeries[2] + ">" +
                                                 "</a>" +
                                            "</div>" +
                                        "</div>" +
                                        "<div class=\"col-md-7\">" +
                                            "<a href=series?id=" + currentSeries[1] + ">" +
                                                "<p>" + currentSeries[0] + "</p>" +
                                             "</a>" +
                                        "</div>" +
                                    "</div>");
                        }

                        var firstSeries = data["series"][0];
                        console.log("First series found: {'Name': '" + firstSeries[0] + "', 'Id': '" + firstSeries[1] +
                                "', 'ImageURL': '" + firstSeries[2] + "'}");
                    }

                    if (data["people"].length > 0) {
                        $('#people').append("<h1>People</h1>");

                        for (var i = 0; i < data["people"].length; i++) {
                            var currentPerson = data["people"][i];

                            $('#people').append(
                                    "<div class=\"row\">" +
                                        "<div class=\"col-md-3 col-md-offset-2\">" +
                                            "<div class=\"text-center\" id=\"personImage\"" + currentPerson + "\">" +
                                                "<a href=person?id=" + currentPerson[1] + ">" +
                                                    "<img src=" + currentPerson[2] + ">" +
                                                "</a>" +
                                            "</div>" +
                                        "</div>" +
                                        "<div class=\"col-md-7\">" +
                                            "<a href=person?id=" + currentPerson[1] + ">" +
                                                "<p>" + currentPerson[0] + "</p>" +
                                            "</a>" +
                                        "</div>" +
                                    "</div>");
                        }

                        var firstPerson = data["people"][0];
                        console.log("First person found: {'Name': '" + firstPerson[0] + "', 'Id': '" + firstPerson[1] +
                                "', 'ImageURL': '" + firstPerson[2] + "'}");
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
            getSearchResult();
        });
    </script>
</t:wrapper>