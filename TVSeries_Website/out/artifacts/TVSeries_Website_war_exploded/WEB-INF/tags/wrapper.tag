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
            <form class="navbar-form navbar-right" action="searchResult.jsp" role="search">
                <div class="form-group">
                    <input type="text" class="form-control" placeholder="Search">
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
    </script>
    <!-- jQuery (necessary http://i.imgur.com/d9AZyDt.jpgfor Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="bootstrap/js/bootstrap.min.js"></script>
    </body>
</html>