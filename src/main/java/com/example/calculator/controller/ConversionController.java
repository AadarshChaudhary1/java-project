package com.example.calculator.controller;

import com.example.calculator.service.ConversionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversions")
public class ConversionController {
    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping
    public ConversionService.ConversionResult convert(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convert(request.category(), request.conversion(), request.value());
    }

    public record ConversionRequest(
            @NotNull ConversionService.Category category,
            @NotNull ConversionService.Conversion conversion,
            @NotNull Double value) {
    }
}
