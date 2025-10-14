package com.arya.edge.point.tools;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PolygonAlgorithm {

    private static final double FLOAT_VALUE = 0.1;
    private static final Integer DEGREE = 2;

    /**
     * 提取边缘点集
     */
    private static List<Point2D> edgePoints = new ArrayList<>();
    /**
     * 顺序边缘点集
     */
    private static List<Point2D> sortedPoints = new ArrayList<>();

    /**
     * 逆时针计算多边形点顺序
     *
     * @param points 边缘点坐标列表
     * @return
     */
    private static List<Point2D> counterclockwisePoints(List<Point2D> points) {
        int size = points.size();
        double sumX = 0, sumY = 0;

        for (Point2D point : points) {
            sumX += point.getX();
            sumY += point.getY();
        }

        double interiorX = sumX / size;
        double interiorY = sumY / size;

        points.sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.getY() - interiorY, p1.getX() - interiorX);
            double angle2 = Math.atan2(p2.getY() - interiorY, p2.getX() - interiorX);
            return Double.compare(angle1, angle2);
        });

        return points;
    }

    public static double calculateTheoArea() {
        List<Point2D> sortedByX = sortedPoints.stream().sorted(Comparator.comparing(Point2D::getX)).toList();
        if (sortedByX.isEmpty()) {
            return 0;
        }

        Point2D lPoint = sortedByX.get(0);
        Point2D rPoint = sortedByX.get(sortedPoints.size() - 1);

        double slope = (rPoint.getY() - lPoint.getY()) / (rPoint.getX() - lPoint.getX());
        double intercept = rPoint.getY() - slope * rPoint.getX();

        List<Point2D> points = new ArrayList<>();
        for (Point2D point : edgePoints) {
            if (slope * point.getX() + intercept <= point.getY()) {
                new Point2D.Float().setLocation(point.getX(), point.getY());
            }
        }

        return calculateArea(points);
    }

    /**
     * 计算面积
     */
    public static double calculateArea(List<Point2D> points) {
        if (points.isEmpty() || points.size() < 3) {
            return 0;
        }
        GeometryFactory geometryFactory = new GeometryFactory();

        Coordinate[] coordinates = new Coordinate[points.size() + 1];
        for (int i = 0; i < points.size(); i++) {
            coordinates[i] = new Coordinate(points.get(i).getX(), points.get(i).getY());
        }

        coordinates[points.size()] = coordinates[0];
        Polygon polygon = geometryFactory.createPolygon(coordinates);

        return polygon.getArea();
    }
}

class KDTree {
    private static class Node {
        double[] point;
        Node left, right;

        Node(double[] point) {
            this.point = point;
        }
    }

    private Node root;
    private final int k;

    public KDTree(int k) {
        this.k = k;
    }

    public void insert(double[] point) {
        root = insertRec(root, point, 0);
    }

    private Node insertRec(Node node, double[] point, int depth) {
        if (node == null) {
            return new Node(point);
        }

        int axis = depth % k;

        if (point[axis] < node.point[axis]) {
            node.left = insertRec(node.left, point, depth + 1);
        } else {
            node.right = insertRec(node.right, point, depth + 1);
        }

        return node;
    }

    public double[] nearestNeighbor(double[] target) {
        return nearestNeighborRec(root, target, 0, null, Double.MAX_VALUE);
    }

    private double[] nearestNeighborRec(Node node, double[] target, int depth, double[] best, double bestDist) {
        if (node == null) {
            return best;
        }

        double dist = euclideanDistance(node.point, target);
        if (dist < bestDist) {
            best = node.point;
            bestDist = dist;
        }

        int axis = depth % k;
        Node nextNode = target[axis] < node.point[axis] ? node.left : node.right;
        Node otherNode = target[axis] < node.point[axis] ? node.right : node.left;

        best = nearestNeighborRec(nextNode, target, depth + 1, best, bestDist);
        bestDist = euclideanDistance(best, target);

        if (Math.abs(target[axis] - node.point[axis]) < bestDist) {
            best = nearestNeighborRec(otherNode, target, depth + 1, best, bestDist);
        }

        return best;
    }

    public double euclideanDistance(double[] p1, double[] p2) {
        double sum = 0;
        for (int i = 0; i < k; i++) {
            sum += Math.pow(p2[i] - p1[i], 2);
        }
        return Math.sqrt(sum);
    }
}
