package com.example.calculator.service;

import org.springframework.stereotype.Service;

@Service
public class BmiService {
    public BmiResult calculate(double weightKg, double heightMeters) {
        requirePositiveFinite(weightKg, "Weight must be a positive number.");
        requirePositiveFinite(heightMeters, "Height must be a positive number.");

        double bmi = weightKg / (heightMeters * heightMeters);
        if (!Double.isFinite(bmi)) {
            throw new IllegalArgumentException("BMI result is outside the supported numeric range.");
        }

        String category = bmi < 18.5 ? "Underweight"
                : bmi < 24.9 ? "Normal weight"
                : bmi < 29.9 ? "Overweight"
                : "Obesity";
        return new BmiResult(bmi, category);
    }

    private void requirePositiveFinite(double value, String message) {
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public record BmiResult(double bmi, String category) {
    }
}
