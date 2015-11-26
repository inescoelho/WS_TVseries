package server;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Input data sent by other applications to our server
 */
public class InputData {

    private String message;
    private int code;
    private ArrayList<Integer> list;
    private int[] array;

    public InputData(@JsonProperty("message") String message, @JsonProperty("code") int code,
                     @JsonProperty("list") ArrayList<Integer> list, @JsonProperty("array") int[] array) {
        this.message = message;
        this.code = code;
        this.list = list;
        this.array = array;
    }

    public String toString() {
        return "{'message': '" + message + "', 'code': " + code + ", 'list': " + list + ", 'array': " +
                Arrays.toString(array) + "}" ;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setList(ArrayList<Integer> list) {
        this.list = list;
    }

    public ArrayList<Integer> getList() {
        return list;
    }

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }
}
