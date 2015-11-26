package server;

/**
 * Output data sent from our server as a response to a given request
 */
public class OutputData {

    private String message;
    private int resultCode;
    private CustomObject object;

    public OutputData(String message, int resultCode) {
        this.message = message;
        this.resultCode = resultCode;
        this.object = new CustomObject();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public CustomObject getObject() {
        return object;
    }

    public void setObject(CustomObject object) {
        this.object = object;
    }
}
