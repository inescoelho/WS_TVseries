package actions.model;


public class Client {

    private String username;

    public Client() {
        username = "";
    }

    public void doClientLogin(String username) {
        // FIXME: Change this??
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
