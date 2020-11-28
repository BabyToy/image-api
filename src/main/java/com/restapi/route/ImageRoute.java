package com.restapi.route;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;

import spark.Spark;

public class ImageRoute {
    private File storageDir;

    public ImageRoute() {
        this.storageDir = new File("storage");
        if (!this.storageDir.isDirectory()) {
            this.storageDir.mkdir();
        }
    }

    public void route() {
        Spark.port(4200);

        // parameter: id
        Spark.get("/api/image", "multipart/form-data", (request, response) -> {
            String id = request.queryParams("id");
            if (id == null) {
                response.status(400);
                return "Parameter ID is required";
            }
            Path path = Paths.get(this.storageDir + "/" + id + ".jpg");
            File file = path.toFile();
            if (!file.exists())  {
                response.status(400);
                return "Image does not exist";
            }
            byte[] data;
            try {
                data = Files.readAllBytes(path);
            } catch (Exception e) {
                response.status(500);
                return "Unable to fetch image: " + e.getMessage();
            }
            HttpServletResponse raw = response.raw();
            response.header("Content-Disposition", "attachment; filename=image.jpg");
            response.type("application/force-download");
            try {
                raw.getOutputStream().write(data);
                raw.getOutputStream().flush();
                raw.getOutputStream().close();
            } catch (Exception e) {
                response.status(500);
                return e.getMessage();
            }
            return raw;
        });

        // parameter: file
        Spark.post("/api/upload", (request, response) -> {
            long limits = 100000000;
            String base = "localhost:4200/api/image";

            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();

            Part fileUpload;
            try {
                fileUpload = request.raw().getPart("file");
            } catch (Exception e) {
                response.status(400);
                return "Key `file` is required in body form-data";
            }

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(storageDir.getName(), limits,
                    limits, 1024);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            String fileName = fileUpload.getSubmittedFileName();
            String extension = FilenameUtils.getExtension(fileName);
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
