<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>

        <div style="margin-top:10px"></div>

            <form action="searchResult.jsp" class="form-horizontal" method="post">
                <div class="row">
                    <div class="col-lg-6 col-lg-offset-3">
                        <div class="input-group">
                            <input type="text" class="form-control" placeholder="Search for...">
                            <span class="input-group-btn">
                                <button class="btn btn-default" type="submit">Go!</button>
                            </span>
                        </div><!-- /input-group -->
                    </div><!-- /.col-lg-6 -->
                </div><!-- /.row -->
            </form>


        <div style="margin-top:20px"></div>

        <h1>Recommended</h1>
        <div class="row row-offcanvas">
            <div class="col-md-2">
                <div class="thumbnail">
                    <a href="default.asp">
                         <img src="http://i.imgur.com/d9AZyDt.jpg" alt="...">
                    </a>
                    <div class="caption">
                        <h3>Thumbnail label</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-2">
                <div class="thumbnail">
                    <a href="default.asp">
                        <img src="http://i.imgur.com/d9AZyDt.jpg" alt="...">
                    </a>
                    <div class="caption">
                        <h3>Thumbnail label</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-2">
                <div class="thumbnail">
                    <a href="default.asp">
                        <img src="http://i.imgur.com/d9AZyDt.jpg" alt="...">
                    </a>
                    <div class="caption">
                        <h3>Thumbnail label</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-2">
                <div class="thumbnail">
                    <a href="default.asp">
                        <img src="http://i.imgur.com/d9AZyDt.jpg" alt="...">
                    </a>
                    <div class="caption">
                        <h3>Thumbnail label</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-2">
                <div class="thumbnail">
                    <a href="default.asp">
                        <img src="http://i.imgur.com/d9AZyDt.jpg" alt="...">
                    </a>
                    <div class="caption">
                        <h3>Thumbnail label</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-2">
                <div class="thumbnail">
                    <a href="default.asp">
                        <img src="http://i.imgur.com/d9AZyDt.jpg" alt="...">

                        <div class="caption">
                            <h3>Thumbnail label</h3>
                        </div>
                    </a>
                </div>
            </div>
        </div>

</t:wrapper>