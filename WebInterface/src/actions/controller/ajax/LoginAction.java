package actions.controller.ajax;


import utils.MyLogger;

import javax.servlet.http.HttpServletRequest;

public class LoginAction extends AjaxAction {

    private String username;
    protected HttpServletRequest request = null;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void doAjaxWork() {

        MyLogger.writeToLogFile("O LoginAction foi chamado " + username);

        // Call client.doClientLogin(username)
        client.doClientLogin(username);

        setAjaxStatus(true);

    }
}
