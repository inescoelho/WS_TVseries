<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>

    <div class="series-header">
        <h1 class="series-title">Series Name</h1>
    </div>

    <div class="row">

        <!-- left column -->
        <div class="col-md-4">
            <div class="text-center">
                <img src="//placehold.it/100" class="avatar img-circle" alt="...">
            </div>
        </div>

        <!-- right column -->
        <div class="col-md-8">
            <p>2010-2014</p>
            <b>Storyline: </b> Lorem ipsum dolor sit amet, consectetur adipiscing elit. In sit amet magna at ipsum luctus volutpat non pellentesque odio. Fusce faucibus mi at condimentum finibus.
        </div>
    </div>

    <div class="container">
        <p><b>Description: </b></p>
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. In sit amet magna at ipsum luctus volutpat non pellentesque odio.
            Fusce faucibus mi at condimentum finibus. Cras quis nibh pulvinar, accumsan lorem non, facilisis neque.
            Curabitur sit amet lobortis eros, nec blandit ex. Nulla quis leo posuere erat dapibus varius.
            Nam placerat, diam eget tempor malesuada, dolor ante finibus dolor, quis sodales magna nibh mollis arcu.
            Aliquam odio nisl, aliquam ac lorem ut, commodo pellentesque nibh.
            Fusce iaculis, erat in congue aliquam, turpis leo ullamcorper ex, et facilisis leo tellus vitae justo. Donec nec sodales lorem, et iaculis ipsum.</p>
        <p>Sed efficitur, felis et euismod ornare, sapien mauris sodales tortor, nec finibus odio dui sed eros.
            Vivamus in enim at erat accumsan efficitur. In hac habitasse platea dictumst.
            Sed scelerisque porttitor dolor, in accumsan dolor condimentum a. Integer pellentesque pellentesque varius.
            Etiam non dolor eu mauris cursus luctus. Cras iaculis leo in tellus pharetra, at aliquet diam posuere.
            Vestibulum efficitur ipsum libero, in maximus sapien tincidunt at.</p>
    </div>

    <div class="container">
        <p><b>Created by: </b></p>
        <div class="row">
            <!-- left column -->
            <div class="col-md-3 col-md-offset-2">
                <div class="text-center">
                    <img src="//placehold.it/100" class="avatar img-circle" alt="...">
                </div>
            </div>

            <!-- right column -->
            <div class="col-md-7">
                <p>Name</p>
            </div>
        </div>
    </div>

    <div class="container">
        <p><b>Actors: </b></p>
        <div class="row">
            <!-- left column -->
            <div class="col-md-3 col-md-offset-2">
                <div class="text-center">
                    <img src="//placehold.it/100" class="avatar img-circle" alt="...">
                </div>
            </div>

            <!-- right column -->
            <div class="col-md-7">
                <p>Name</p>
            </div>
        </div>
        <div class="row">
            <!-- left column -->
            <div class="col-md-3 col-md-offset-2">
                <div class="text-center">
                    <img src="//placehold.it/100" class="avatar img-circle" alt="...">
                </div>
            </div>

            <!-- right column -->
            <div class="col-md-7">
                <p>Name</p>
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
                        $('div>div>div>ul').append("<li><a href='listSeries?category=" + currentGenre["type"] + "'>"
                                + currentGenre["type"] + "</a></li>");
                    }
                },
                error: function(jqXHR, exception) {
                    console.log(jqXHR.status);
                    console.log(exception);
                }
            });
        }
</t:wrapper>