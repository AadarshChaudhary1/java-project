package com.example.calculator.controller;

import com.example.calculator.service.GuessingGameService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games/guess")
public class GuessingGameController {
    private final GuessingGameService gameService;

    public GuessingGameController(GuessingGameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public GuessingGameService.GameStarted start() {
        return gameService.start();
    }

    @PostMapping("/{gameId}")
    public GuessingGameService.GuessResult guess(
            @PathVariable String gameId, @Valid @RequestBody GuessRequest request) {
        return gameService.guess(gameId, request.guess());
    }

    public record GuessRequest(@NotNull @Min(1) @Max(100) Integer guess) {
    }
}
