package ro.utcn.kdd.rosil.api;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class RosilApi {

    private static int v = 2;

    public static void main(String[] args) {
        System.out.println(v);
        System.out.println(foo());
        System.out.println(v);
        /*
        final RosilTracer tracer = new RosilTracer();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        get("/rosil/:word",
                (req, res) -> tracer.splitWord(req.params("word")), gson::toJson);
        */
    }

    private static int foo() {
        return v++;
    }
}