package com.restapi;

import com.restapi.route.ImageRoute;

public class App {
    public static void main(String[] args) {
        ImageRoute router = new ImageRoute();
        router.route();
    }
}
