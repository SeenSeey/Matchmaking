package com.example.matcmaking_api.exception;

public class PlayerAlreadyInGameException extends RuntimeException {
    public PlayerAlreadyInGameException(String playerId) {
        super(String.format("Игрок с ID %s уже находится в игре", playerId));
    }
}
