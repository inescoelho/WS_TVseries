$(document).ready(function() {
    alert("KSKSK");
    console.log("Estou no ready");
    $("form#inputfiledata").submit(function() {

        console.log("AQUI");

        console.log($('#username').val());

    });
});
