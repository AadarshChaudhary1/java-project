package com.example.calculator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculatorServiceTest {
    private final BmiService bmiService = new BmiService();
    private final MathService mathService = new MathService();
    private final ConversionService conversionService = new ConversionService();
    private final GeometryService geometryService = new GeometryService();

    @Test
    void calculatesBmiAndPreservesOriginalCategoryBoundaries() {
        assertEquals(22.0, bmiService.calculate(88, 2).bmi());
        assertEquals("Normal weight", bmiService.calculate(88, 2).category());
        assertEquals("Overweight", bmiService.calculate(24.9, 1).category());
    }

    @Test
    void validatesMathOperationsAndComputesFactorial() {
        assertEquals(120.0, mathService.calculate(MathService.Operation.FACTORIAL, 5.0, null));
        assertEquals(0.5, mathService.calculate(MathService.Operation.SIN, 30.0, null), 1e-12);
        assertThrows(IllegalArgumentException.class,
                () -> mathService.calculate(MathService.Operation.DIVIDE, 1.0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> mathService.calculate(MathService.Operation.SQRT, -1.0, null));
    }

    @Test
    void convertsUnitsWithOriginalFactorsAndRejectsMismatchedCategories() {
        assertEquals(3.28084, conversionService.convert(
                ConversionService.Category.LENGTH, ConversionService.Conversion.METERS_TO_FEET, 1).result());
        assertEquals(32.0, conversionService.convert(
                ConversionService.Category.TEMPERATURE, ConversionService.Conversion.CELSIUS_TO_FAHRENHEIT, 0).result());
        assertThrows(IllegalArgumentException.class, () -> conversionService.convert(
                ConversionService.Category.WEIGHT, ConversionService.Conversion.METERS_TO_FEET, 1));
    }

    @Test
    void calculatesGeometryAndRejectsMissingDimensions() {
        assertEquals(Math.PI * 4, geometryService.area(GeometryService.Shape.CIRCLE, 2, null).result());
        assertEquals(24.0, geometryService.volume(GeometryService.Shape.CUBOID, 2, 3.0, 4.0).result());
        assertThrows(IllegalArgumentException.class,
                () -> geometryService.area(GeometryService.Shape.RECTANGLE, 2, null));
    }
}
