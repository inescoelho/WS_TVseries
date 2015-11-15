package application;

public class LoginAction {

    private String username;

    public String doLogin() throws Exception {
        System.out.println("Got " + username);

        if (!username.equals("username"))
            return "error";

        return "success";
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
