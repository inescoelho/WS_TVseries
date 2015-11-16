function setLoginStatus(clientName) {
    if (clientName == "null") {
        onLoginFailure();
    } else {
        // Do not call onLoginSuccess because we will do more things there but
        // FIXME: Change it to call onLoginSuccess when we know what do we need to pass to that function
        $('#welcome_user').text('Welcome ' + clientName);
        $('#welcome_message_li').show();
        $('#login_li').hide();
    }
}