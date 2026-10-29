package com.example.calculator.service;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class GuessingGameService {
    private final ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();

    public GameStarted start() {
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new Game(ThreadLocalRandom.current().nextInt(1, 101)));
        return new GameStarted(gameId, "A number between 1 and 100 has been chosen.");
    }

    public GuessResult guess(String gameId, int guess) {
        if (guess < 1 || guess > 100) {
            throw new IllegalArgumentException("Guess must be between 1 and 100.");
        }
        Game game = games.get(gameId);
        if (game == null) {
            throw new NoSuchElementException("Game not found or already completed. Start a new game.");
        }
        synchronized (game) {
            game.attempts++;
            if (guess == game.number) {
                games.remove(gameId, game);
                return new GuessResult("Correct! You guessed the number.", game.attempts, true);
            }
            return new GuessResult(guess < game.number ? "Too low! Try again." : "Too high! Try again.",
                    game.attempts, false);
        }
    }

    private static final class Game {
        private final int number;
        private int attempts;

        private Game(int number) {
            this.number = number;
        }
    }

    public record GameStarted(String gameId, String message) {
    }

    public record GuessResult(String message, int attempts, boolean correct) {
    }
}
