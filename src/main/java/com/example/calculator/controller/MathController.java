package com.example.calculator.controller;

import com.example.calculator.service.MathService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/math")
public class MathController {
    private final MathService mathService;

    public MathController(MathService mathService) {
        this.mathService = mathService;
    }

    @PostMapping
    public MathResponse calculate(@Valid @RequestBody MathRequest request) {
        return new MathResponse(request.operation(), mathService.calculate(
                request.operation(), request.firstValue(), request.secondValue()));
    }

    public record MathRequest(
            @NotNull MathService.Operation operation,
            @NotNull Double firstValue,
            Double secondValue) {
    }

    public record MathResponse(MathService.Operation operation, double result) {
    }
}
