<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>

    <div>
        <div class="page-header">
            <h1>Series from category ${requestScope.category}</h1>
        </div>

        <div id="series">
        </div>
    </div>

    <script>
        function getSeriesFromGenre() {

            $.ajax({
                type: "GET",
                url: "http://localhost:8090/getGenres",
                async: true,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.setRequestHeader("Accept", "application/json")
                },
                success: function (data) {
                    for (var i = 0; i < data.length; i++) {
                        var currentGenre = data[i];

                        if (currentGenre["type"] == "${requestScope.category}")
                        {
                            for (var j=0; j < currentGenre["series"].length; j++)
                            {
                                var currentSeries = currentGenre["series"][j];

                                $('#series').append(
                                        "<div class=\"row\">" +
                                            "<div class=\"col-md-3 col-md-offset-2\">" +
                                                "<div class=\"text-center\" id=\"seriesImage\"" + currentSeries + "\">" +
                                                    "<a href=series?id=" + currentSeries[1] + ">" +
                                                        "<img src=" + currentSeries[2] + "width=\"250\" height=\"200\">" +
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
            getSeriesFromGenre();
            getGenres();
            performRecommendation();
        });
    </script>
</t:wrapper>