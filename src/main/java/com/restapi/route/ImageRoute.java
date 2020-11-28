package com.restapi.route;

import spark.Spark;

public class ImageRoute {

    public void ImageRoute() {
    }

    public void route() {
      Spark.port(4200);

      Spark.get("/api/image", (request, response) -> {
            return "Return from GET";
        });

        Spark.post("/api/upload", (request, response) -> {
            return "Return from POST";
        });
    }
}
