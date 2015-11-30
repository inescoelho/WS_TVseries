<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <div class="alert">

    </div>

    <script>
        $(document).ready(function() {
            var genre = $.urlParam('category');
            $('alert').append("<h2>" + genre + "</h2>");
            getGenres();
        });
    </script>
</t:wrapper>