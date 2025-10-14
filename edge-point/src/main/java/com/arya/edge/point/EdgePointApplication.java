package com.arya.edge.point;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tools.jackson.databind.ObjectMapper;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@SpringBootApplication
public class EdgePointApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgePointApplication.class, args);

        InputStream inputStream = EdgePointApplication.class.getClassLoader().getResourceAsStream("data/points.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Point2D plots = new ObjectMapper().readValue(reader, Point2D.class);

    }

}
