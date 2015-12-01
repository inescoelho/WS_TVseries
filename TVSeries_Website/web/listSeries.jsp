<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>

    <div class="page-header">
        <h1>Series from category ${requestScope.category}</h1>
    </div>

    <div class="container col-sm-4 col-md-3 col-sm-offset-1 col-md-offset-1">
         <div class="list-group" id="listOfSeriesFromCategory"></div>
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
                                $('#listOfSeriesFromCategory').append("<a class=\"list-group-item\" href='series?id=" + currentGenre["series"][j][1]
                                        + "'>" + currentGenre["series"][j][0] + "</a>");
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
        });
    </script>
</t:wrapper>