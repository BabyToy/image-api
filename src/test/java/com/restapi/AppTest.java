package com.restapi;

import java.io.File;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void urlListValidForZip() {
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("localhost:4200/api/upload")
                    .field("file", new File("/D:/Dev/angular/StudentAssets/ApiImages/images/products/products.zip"))
                    .asString();

            String body = response.getBody();
            String[] urls = body.split(",");
            assertTrue(urls.length > 0);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void urlListValidForSingleFile() {
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("localhost:4200/api/upload")
                    .field("file", new File("/D:/Dev/angular/AutoParts/client/src/assets/images/hero1.jpg")).asString();

            String body = response.getBody();
            String[] urls = body.split(",");
            assertEquals(urls.length, 1);
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
