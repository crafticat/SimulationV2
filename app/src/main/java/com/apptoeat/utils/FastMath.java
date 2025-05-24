package com.apptoeat.utils;

public class FastMath {

    public static double dist(double x1, double y1, double x2, double y2) {
        return (y1 - y2) * (y1 - y2) + (x1 - x2) * (x1 - x2);
    }
}
