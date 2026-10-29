package com.example.calculator.service;

import org.springframework.stereotype.Service;

@Service
public class ConversionService {
    public ConversionResult convert(Category category, Conversion conversion, double value) {
        if (category == null || conversion == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException("Choose a conversion and enter a finite value.");
        }
        if (category != Category.TEMPERATURE && value <= 0) {
            throw new IllegalArgumentException("Value must be positive for this conversion.");
        }

        double result = switch (conversion) {
            case METERS_TO_FEET -> inCategory(category, Category.LENGTH, value * 3.28084);
            case FEET_TO_METERS -> inCategory(category, Category.LENGTH, value / 3.28084);
            case KILOMETERS_TO_MILES -> inCategory(category, Category.LENGTH, value * 0.621371);
            case MILES_TO_KILOMETERS -> inCategory(category, Category.LENGTH, value / 0.621371);
            case KILOGRAMS_TO_POUNDS -> inCategory(category, Category.WEIGHT, value * 2.20462);
            case POUNDS_TO_KILOGRAMS -> inCategory(category, Category.WEIGHT, value / 2.20462);
            case GRAMS_TO_OUNCES -> inCategory(category, Category.WEIGHT, value * 0.035274);
            case OUNCES_TO_GRAMS -> inCategory(category, Category.WEIGHT, value / 0.035274);
            case CELSIUS_TO_FAHRENHEIT -> inCategory(category, Category.TEMPERATURE, value * 9 / 5 + 32);
            case FAHRENHEIT_TO_CELSIUS -> inCategory(category, Category.TEMPERATURE, (value - 32) * 5 / 9);
            case CELSIUS_TO_KELVIN -> inCategory(category, Category.TEMPERATURE, value + 273.15);
            case KELVIN_TO_CELSIUS -> inCategory(category, Category.TEMPERATURE, value - 273.15);
            case LITERS_TO_GALLONS -> inCategory(category, Category.VOLUME, value * 0.264172);
            case GALLONS_TO_LITERS -> inCategory(category, Category.VOLUME, value / 0.264172);
            case MILLILITERS_TO_FLUID_OUNCES -> inCategory(category, Category.VOLUME, value * 0.033814);
            case FLUID_OUNCES_TO_MILLILITERS -> inCategory(category, Category.VOLUME, value / 0.033814);
            case SQUARE_METERS_TO_SQUARE_FEET -> inCategory(category, Category.AREA, value * 10.7639);
            case SQUARE_FEET_TO_SQUARE_METERS -> inCategory(category, Category.AREA, value / 10.7639);
            case HECTARES_TO_ACRES -> inCategory(category, Category.AREA, value * 2.47105);
            case ACRES_TO_HECTARES -> inCategory(category, Category.AREA, value / 2.47105);
        };
        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("The converted value is outside the supported numeric range.");
        }
        return new ConversionResult(category, conversion, value, result);
    }

    private double inCategory(Category actual, Category expected, double result) {
        if (actual != expected) {
            throw new IllegalArgumentException("The selected conversion does not match the conversion category.");
        }
        return result;
    }

    public enum Category {
        LENGTH, WEIGHT, TEMPERATURE, VOLUME, AREA
    }

    public enum Conversion {
        METERS_TO_FEET, FEET_TO_METERS, KILOMETERS_TO_MILES, MILES_TO_KILOMETERS,
        KILOGRAMS_TO_POUNDS, POUNDS_TO_KILOGRAMS, GRAMS_TO_OUNCES, OUNCES_TO_GRAMS,
        CELSIUS_TO_FAHRENHEIT, FAHRENHEIT_TO_CELSIUS, CELSIUS_TO_KELVIN, KELVIN_TO_CELSIUS,
        LITERS_TO_GALLONS, GALLONS_TO_LITERS, MILLILITERS_TO_FLUID_OUNCES, FLUID_OUNCES_TO_MILLILITERS,
        SQUARE_METERS_TO_SQUARE_FEET, SQUARE_FEET_TO_SQUARE_METERS, HECTARES_TO_ACRES, ACRES_TO_HECTARES
    }

    public record ConversionResult(Category category, Conversion conversion, double input, double result) {
    }
}
