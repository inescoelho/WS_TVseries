<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>

    <h1>All your favorites series in one place!</h1>
    <h3>But just your favorites...</h3>
    <h5>(We hope to have your favourites)</h5>

    <script>
        $(document).ready(function() {
            getGenres();
        });
    </script>
</t:wrapper>