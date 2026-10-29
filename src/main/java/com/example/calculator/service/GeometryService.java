package com.example.calculator.service;

import org.springframework.stereotype.Service;

@Service
public class GeometryService {
    public GeometryResult area(Shape shape, double first, Double second) {
        requireShapeAndPositive(shape, first);
        double result = switch (shape) {
            case CIRCLE -> Math.PI * first * first;
            case SQUARE -> first * first;
            case RECTANGLE -> first * requireSecond(second);
            case TRIANGLE -> 0.5 * first * requireSecond(second);
            default -> throw new IllegalArgumentException("This shape is not supported by the area calculator.");
        };
        return result(shape, result);
    }

    public GeometryResult volume(Shape shape, double first, Double second, Double third) {
        requireShapeAndPositive(shape, first);
        double result = switch (shape) {
            case SPHERE -> (4.0 / 3.0) * Math.PI * Math.pow(first, 3);
            case CUBE -> Math.pow(first, 3);
            case CYLINDER -> Math.PI * Math.pow(first, 2) * requireSecond(second);
            case CUBOID -> first * requireSecond(second) * requireSecond(third);
            case CONE -> (1.0 / 3.0) * Math.PI * Math.pow(first, 2) * requireSecond(second);
            default -> throw new IllegalArgumentException("This shape is not supported by the volume calculator.");
        };
        return result(shape, result);
    }

    private void requireShapeAndPositive(Shape shape, double value) {
        if (shape == null) {
            throw new IllegalArgumentException("Choose a shape.");
        }
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive finite numbers.");
        }
    }

    private double requireSecond(Double value) {
        if (value == null || !Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException("This shape requires a second positive finite dimension.");
        }
        return value;
    }

    private GeometryResult result(Shape shape, double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("The result is outside the supported numeric range.");
        }
        return new GeometryResult(shape, value);
    }

    public enum Shape {
        CIRCLE, RECTANGLE, TRIANGLE, SQUARE, SPHERE, CUBE, CYLINDER, CUBOID, CONE
    }

    public record GeometryResult(Shape shape, double result) {
    }
}
