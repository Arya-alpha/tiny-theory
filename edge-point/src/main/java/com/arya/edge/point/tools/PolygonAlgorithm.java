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
     * 从给定点集中提取凸包边缘点
     * 
     * @param points 输入点集
     * @return 凸包边缘点集（按逆时针顺序）
     */
    public static List<Point2D> getConvexHull(List<Point2D> points) {
        if (points == null || points.isEmpty() || points.size() < 3) {
            return new ArrayList<>();
        }
        
        List<Point2D> hull = graham(points);
        // 更新成员变量
        edgePoints = new ArrayList<>(hull);
        sortedPoints = counterclockwisePoints(edgePoints);
        
        return new ArrayList<>(hull);
    }

    /**
     * 使用Graham扫描算法计算凸包
     * 
     * @param points 输入点集
     * @return 凸包的点集
     */
    private static List<Point2D> graham(List<Point2D> points) {
        if (points.size() < 3) {
            return new ArrayList<>(points);
        }

        // 1. 找到最底部的点（y坐标最小，如果相同则x坐标最小）
        Point2D bottomPoint = points.get(0);
        int bottomIndex = 0;
        for (int i = 1; i < points.size(); i++) {
            Point2D current = points.get(i);
            if (current.getY() < bottomPoint.getY() || 
                (current.getY() == bottomPoint.getY() && current.getX() < bottomPoint.getX())) {
                bottomPoint = current;
                bottomIndex = i;
            }
        }

        // 2. 将底部点移到列表开头
        List<Point2D> sorted = new ArrayList<>(points);
        Point2D temp = sorted.get(0);
        sorted.set(0, sorted.get(bottomIndex));
        sorted.set(bottomIndex, temp);

        final Point2D startPoint = sorted.get(0);

        // 3. 按极角排序（相对于底部点）
        sorted.subList(1, sorted.size()).sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.getY() - startPoint.getY(), p1.getX() - startPoint.getX());
            double angle2 = Math.atan2(p2.getY() - startPoint.getY(), p2.getX() - startPoint.getX());
            
            if (Math.abs(angle1 - angle2) < 1e-10) {
                // 如果极角相同，按距离排序
                double dist1 = startPoint.distance(p1);
                double dist2 = startPoint.distance(p2);
                return Double.compare(dist1, dist2);
            }
            return Double.compare(angle1, angle2);
        });

        // 4. 使用栈构建凸包
        List<Point2D> hull = new ArrayList<>();
        hull.add(sorted.get(0));
        
        for (int i = 1; i < sorted.size(); i++) {
            // 移除导致右转的点
            while (hull.size() > 1 && 
                   crossProduct(hull.get(hull.size() - 2), hull.get(hull.size() - 1), sorted.get(i)) <= 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(sorted.get(i));
        }

        return hull;
    }

    /**
     * 计算三个点的叉积，用于判断是否右转
     * @param o 起点
     * @param a 中间点
     * @param b 终点
     * @return 叉积值，<0表示右转，>0表示左转，=0表示共线
     */
    private static double crossProduct(Point2D o, Point2D a, Point2D b) {
        return (a.getX() - o.getX()) * (b.getY() - o.getY()) - (a.getY() - o.getY()) * (b.getX() - o.getX());
    }

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
