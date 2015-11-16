function onLoginSuccess(data) {
    console.log('Got username ' + data['username']);

    $('#welcome_user').text('Welcome ' + data['username']);
    $('#welcome_message_li').show();
    $('#login_li').hide();

    //Get recommendations (Gotta be somewhere in data)
}


function onLoginFailure() {
    console.log("On Login Failure");

    $('#welcome_message_li').hide();
    $('#login_li').show();
}
