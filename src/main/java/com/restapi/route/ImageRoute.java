package com.restapi.route;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;

import spark.Spark;

public class ImageRoute {

    private File storageDir;

    public ImageRoute() {
        this.storageDir = new File("storage");
        System.out.println("Path: " + this.storageDir.getAbsolutePath());
        if (!this.storageDir.isDirectory()) {
            this.storageDir.mkdir();
        }
    }

    public void route() {
        Spark.port(4200);

        Spark.get("/api/image", "multipart/form-data", (request, response) -> {
            return "Return from GET";
        });

        long limits = 100000000;
        Spark.post("/api/upload", (request, response) -> {
            String base = "localhost:4200/api/image";
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(storageDir.getName(), limits, limits, 1024);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            Part fileUpload = request.raw().getPart("file");
            String fileName = fileUpload.getSubmittedFileName();
            String extension =  FilenameUtils.getExtension(fileName);
            Path target = Paths.get(this.storageDir + "/" + id + "." + extension);
            try (final InputStream streamIn = fileUpload.getInputStream()) {
                Files.copy(streamIn, target);
                fileUpload.delete();
            }
            multipartConfigElement = null;
            fileUpload = null;

            return base + "?id=" + id;
        });
    }
}
