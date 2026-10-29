package com.example.calculator.controller;

import com.example.calculator.service.BmiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/bmi")
public class BmiController {
    private final BmiService bmiService;

    public BmiController(BmiService bmiService) {
        this.bmiService = bmiService;
    }

    @PostMapping
    public BmiService.BmiResult calculate(@Valid @RequestBody BmiRequest request) {
        return bmiService.calculate(request.weightKg(), request.heightMeters());
    }

    public record BmiRequest(@NotNull @Positive Double weightKg, @NotNull @Positive Double heightMeters) {
    }
}
