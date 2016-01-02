<%@tag description="Simple Wrapper Tag" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>TV Series</title>

    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <link href="bootstrap/css/dashboard.css" rel="stylesheet">
</head>

<body>
    <script src="jquery.min.js"></script>

    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <h3 class="text-muted"><b>TV Series</b></h3>
        </div>

        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li><a href="index.jsp">Home</a></li>
            </ul>
            <form class="navbar-form navbar-right" action="search" role="search">
                <div class="form-group">
                    <input type="text" class="form-control" placeholder="Search" name="searchQuery">
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
            </form>
        </div>
    </nav>


    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-3 col-md-2 sidebar">
                <ul class="nav nav-sidebar" id="listGenres">
                </ul>
            </div>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                 <jsp:doBody/>

                <div class="spacer">&nbsp;</div>
                <div class="spacer">&nbsp;</div>

                    <div class="jumbotron">
                        <h1>Recommended</h1>
                        <div class="spacer">&nbsp;</div>

                        <div class="row row-offcanvas" id="recommendedSeries">

                        </div>
                    </div>

            </div>
        </div>
    </div>


    <script>
        function getGenres() {
            $.ajax({
                type: "GET",
                url: "http://localhost:8090/getGenres",
                async: true,
                beforeSend: function (xhr){
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.setRequestHeader("Accept", "application/json")
                },
                success: function(data) {
                    console.log("Got " + data.length + " genres:");

                    console.log("================================================================================");

                    for (var i=0; i < data.length; i++) {
                        var currentGenre = data[i];
                        $('#listGenres').append("<li><a href='listSeries?category=" + currentGenre["type"] + "'>"
                                + currentGenre["type"] + "</a></li>");
                    }
                },
                error: function(jqXHR, exception) {
                    console.log(jqXHR.status);
                    console.log(exception);
                }
            });
        }

        function performRecommendation() {
            $.ajax({
                type: "GET",
                url: "http://localhost:8090/getRecommendation",
                async: true,
                beforeSend: function (xhr){
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.setRequestHeader("Accept", "application/json")
                },
                success: function(data) {

                    var series = data["series"];
                    console.log("Got " + series.length + " series");

                    for (var i=0; i < series.length; i++) {
                        console.log("Got series: " + series[i][0] + " " + series[i][1] +  " " + series[i][2]);

                        $('#recommendedSeries').append(
                                "<div class=\"col-md-1\">" +
                                    "<div class=\"thumbnail\">" +
                                        "<a href=series?id=" + series[i][1] + ">" +
                                            "<img src=" + series[i][2] + "width=\"150\" height=\"100\">" +
                                        "</a>" +
                                        "<div class=\"caption\">" +
                                            series[i][0] +
                                        "</div>" +
                                    "</div>" +
                                "</div>");
                    }

                    var people = data["people"];
                    console.log("Got " + people.length + " people");

                    for (i=0; i < people.length; i++) {
                        console.log("Got person: " + people[i][0] + " " + people[i][1] +  " " + people[i][2]);

                        if (people[i][2] == "")
                            people[i][2] ="http://i.imgur.com/0zxp2G8.jpg";

                        $('#recommendedSeries').append(
                                "<div class=\"col-md-1\">" +
                                    "<div class=\"thumbnail\">" +
                                        "<a href=person?id=" + people[i][1] + ">" +
                                            "<img src=" + people[i][2] + "width=\"150\" height=\"100\">" +
                                        "</a>" +
                                        "<div class=\"caption\">" +
                                            people[i][0] +
                                        "</div>" +
                                    "</div>" +
                                "</div>");
                    }
                },
                error: function(jqXHR, exception) {
                    console.log(jqXHR.status);
                    console.log(exception);
                }
            });
        }
    </script>
    <!-- jQuery (necessary http://i.imgur.com/d9AZyDt.jpgfor Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="bootstrap/js/bootstrap.min.js"></script>
    </body>
</html>