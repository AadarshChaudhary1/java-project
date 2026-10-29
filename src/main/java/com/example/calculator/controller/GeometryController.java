package com.example.calculator.controller;

import com.example.calculator.service.GeometryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geometry")
public class GeometryController {
    private final GeometryService geometryService;

    public GeometryController(GeometryService geometryService) {
        this.geometryService = geometryService;
    }

    @PostMapping("/area")
    public GeometryService.GeometryResult area(@Valid @RequestBody GeometryRequest request) {
        return geometryService.area(request.shape(), request.firstDimension(), request.secondDimension());
    }

    @PostMapping("/volume")
    public GeometryService.GeometryResult volume(@Valid @RequestBody GeometryRequest request) {
        return geometryService.volume(request.shape(), request.firstDimension(),
                request.secondDimension(), request.thirdDimension());
    }

    public record GeometryRequest(
            @NotNull GeometryService.Shape shape,
            @NotNull Double firstDimension,
            Double secondDimension,
            Double thirdDimension) {
    }
}
