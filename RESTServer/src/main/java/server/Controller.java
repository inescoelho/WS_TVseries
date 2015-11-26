package server;

import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    public Controller() {
        // Here load the ontology and extract the individuals and properties from it!
        System.out.println("In the constructor");
    }

    @CrossOrigin
    @RequestMapping(value= "/test", method = RequestMethod.POST, produces="application/json")
    public @ResponseBody OutputData  HelloWorld(@RequestBody InputData inputData) {
        System.out.println("Got message: " + inputData);

        return new OutputData(inputData.getMessage() + " Returned", inputData.getCode() + 1);
    }
}
