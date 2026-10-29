package com.example.calculator.service;

import org.springframework.stereotype.Service;

@Service
public class MathService {
    public double calculate(Operation operation, Double firstValue, Double secondValue) {
        if (operation == null || firstValue == null || !Double.isFinite(firstValue)) {
            throw new IllegalArgumentException("Choose an operation and enter a finite first value.");
        }

        double first = firstValue;
        double result = switch (operation) {
            case ADD -> first + requireSecond(secondValue);
            case SUBTRACT -> first - requireSecond(secondValue);
            case MULTIPLY -> first * requireSecond(secondValue);
            case DIVIDE -> divide(first, requireSecond(secondValue));
            case POWER -> Math.pow(first, requireSecond(secondValue));
            case SQRT -> squareRoot(first);
            case FACTORIAL -> factorial(first);
            case MODULUS -> modulus(first, requireSecond(secondValue));
            case SIN -> Math.sin(Math.toRadians(first));
            case COS -> Math.cos(Math.toRadians(first));
            case TAN -> Math.tan(Math.toRadians(first));
            case LOG10 -> logarithm(first, false);
            case LN -> logarithm(first, true);
            case ABS -> Math.abs(first);
        };
        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("The result is outside the supported numeric range.");
        }
        return result;
    }

    private double requireSecond(Double secondValue) {
        if (secondValue == null || !Double.isFinite(secondValue)) {
            throw new IllegalArgumentException("This operation requires a finite second value.");
        }
        return secondValue;
    }

    private double divide(double first, double second) {
        if (second == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
        return first / second;
    }

    private double squareRoot(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Square root of a negative number is not real.");
        }
        return Math.sqrt(value);
    }

    private double factorial(double value) {
        if (value < 0 || value != Math.floor(value)) {
            throw new IllegalArgumentException("Factorial requires a non-negative integer.");
        }
        if (value > 170) {
            throw new IllegalArgumentException("Factorial is limited to 170 to keep the result finite.");
        }
        double result = 1;
        for (int factor = 2; factor <= (int) value; factor++) {
            result *= factor;
        }
        return result;
    }

    private double modulus(double first, double second) {
        if (second == 0) {
            throw new IllegalArgumentException("Modulus by zero is not allowed.");
        }
        return first % second;
    }

    private double logarithm(double value, boolean natural) {
        if (value <= 0) {
            throw new IllegalArgumentException("Logarithm of a non-positive number is undefined.");
        }
        return natural ? Math.log(value) : Math.log10(value);
    }

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, POWER, SQRT, FACTORIAL, MODULUS,
        SIN, COS, TAN, LOG10, LN, ABS
    }
}
