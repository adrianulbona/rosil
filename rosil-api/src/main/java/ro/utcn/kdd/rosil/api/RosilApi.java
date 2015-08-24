package ro.utcn.kdd.rosil.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ro.utcn.kdd.rosil.RosilTracer;

import static spark.Spark.*;

public class RosilApi {
    public static void main(String[] args) {
        final RosilTracer tracer = new RosilTracer();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        get("/rosil/:word", (req, res) -> tracer.splitWord(req.params("word")), gson::toJson);
    }
}