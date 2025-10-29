package com.arya.edge.point;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@SpringBootApplication
public class EdgePointApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgePointApplication.class, args);

        InputStream inputStream = EdgePointApplication.class.getClassLoader().getResourceAsStream("data/points.json");
        BufferedReader reader = null;
        if (inputStream != null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        List<Point2D.Double> points = new ObjectMapper().readValue(reader, new TypeReference<List<Point2D.Double>>(){});

        customJFrame(points);

//        PolygonAlgorithm.
    }

    static JFrame customJFrame(List<Point2D.Double> points) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        Panel panel = new Panel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                g.setColor(Color.RED);

                // 绘制每一个散点
                for (int i = 0; i < points.size(); i++) {
                    Point2D.Double point = points.get(i);
                    double x = point.getX();
                    double y = point.getY();
//                    int y = points[i][1];
//                    g.fillOval(x, y, 5, 5);
                }
            }
        };
        frame.add(panel);


        frame.setVisible(true);
        return null;
    }
}
