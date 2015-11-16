<%@ page import="actions.model.Client" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Movie Recommendation Application with Semantic Web">
    <meta name="author" content="Inês Coelho; Joaquim Leitão">

    <title>Movie Recommendation Application</title>

    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/heroic-features.css" rel="stylesheet">
    <link href="css/fancy-navbar-theme.css" rel="stylesheet">
    <link href="css/custom.css" rel="stylesheet">
</head>

<body>

<!-- Top Navbar -->
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li>
                    <a href="index.jsp" class="inactiveLink">Home</a>
                </li>
                <li>
                    <a href="contacts.jsp">Contact</a>
                </li>
                <li>
                    <form class="navbar-form navbar-left" role="search">
                        <div class="form-group">
                            <input type="text" class="form-control" placeholder="Search">
                            <a href="#"><img src="lib/images/search_icon.png"/></a>
                        </div>
                    </form>

                </li>
            </ul>

            <ul class="nav navbar-nav navbar-right">
                <li id="welcome_message_li">
                    <a href="#" id="welcome_user" class="inactiveLink"></a>
                </li>
                <li id="login_li" class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><b>Login</b> <span class="caret"></span></a>
                    <ul id="login-dp" class="dropdown-menu">
                        <li>
                            <div class="row">
                                <div class="col-md-12">
                                    <form id="loginForm" method="post" class="form" role="form" accept-charset="UTF-8">
                                        <div class="form-group">
                                            <input type="text" class="form-control" placeholder="Username"
                                                   name="username" id="username" required>
                                        </div>
                                        <div class="form-group">
                                            <button type="submit" class="btn btn-primary btn-block">Sign in</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </li>
                    </ul>
                </li>
            </ul>

        </div>
    </div>
</nav>

<!-- Page Content -->
<div class="container">

    <div class="row">
        <div class="col-lg-12">
            <h3>Recently Viewed</h3>
        </div>
    </div>

    <div class="row text-center">
        <div class="col-md-3 col-sm-6 hero-feature">
            <div class="thumbnail">
                <img src="http://placehold.it/800x500" alt="">
                <div class="caption">
                    <h3>Series A</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3 col-sm-6 hero-feature">
            <div class="thumbnail">
                <img src="http://placehold.it/800x500" alt="">
                <div class="caption">
                    <h3>Series B</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3 col-sm-6 hero-feature">
            <div class="thumbnail">
                <img src="http://placehold.it/800x500" alt="">
                <div class="caption">
                    <h3>Series C</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3 col-sm-6 hero-feature">
            <div class="thumbnail">
                <img src="http://placehold.it/800x500" alt="">
                <div class="caption">
                    <h3>Series D</h3>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <h3>Recommended</h3>
        </div>
    </div>

    <div class="row text-center">
        <div class="col-md-3 col-sm-6 hero-feature">
            <div class="thumbnail">
                <img src="http://placehold.it/800x500" alt="">
                <div class="caption">
                    <h3>Series E</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3 col-sm-6 hero-feature">
            <div class="thumbnail">
                <img src="http://placehold.it/800x500" alt="">
                <div class="caption">
                    <h3>Series F</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3 col-sm-6 hero-feature">
            <div class="thumbnail">
                <img src="http://placehold.it/800x500" alt="">
                <div class="caption">
                    <h3>Series G</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3 col-sm-6 hero-feature">
            <div class="thumbnail">
                <img src="http://placehold.it/800x500" alt="">
                <div class="caption">
                    <h3>Series H</h3>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="js/jquery.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="lib/main.js"></script>
<script src="lib/login.js"></script>
<script>
    // Has to be here so we can run code on server side
    $(document).ready(function() {

        // Check session if client is already logged in
        var clientName = "<%= session.getAttribute("client") == null ? null : (Client)session.getAttribute("client") == null ? null : ((Client)session.getAttribute("client")).getUsername() %>";
        console.log("Client=" + clientName);

        setLoginStatus(clientName);

        $("form#loginForm").submit(function(e) {
            e.preventDefault();
            var formData = new FormData($(this)[0]);
            var pathnameArray = window.location.pathname.split("/");

            $.ajax({
                url: 'http://' + window.location.host + '/' + pathnameArray[1] + '/login.action',
                type: 'POST',
                data: formData,
                dataType: 'json',
                crossDomain: true,
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Accept','text/json');
                },
                async: true,
                success: function (data) {
                    onLoginSuccess(data);
                },
                error: function(xhr, ajaxOptions, thrownError){
                    console.log('Error');
                    onLoginFailure();
                },
                cache: false,
                contentType: false,
                processData: false

            });
        });
    });
</script>

</body>
</html>

