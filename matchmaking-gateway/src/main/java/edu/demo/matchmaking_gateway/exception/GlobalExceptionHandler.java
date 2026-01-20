package edu.demo.matchmaking_gateway.exception;

import com.example.matcmaking_api.dto.StatusResponse;
import com.example.matcmaking_api.exception.PlayerAlreadyInGameException;
import com.example.matcmaking_api.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StatusResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        StatusResponse response = new StatusResponse(null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PlayerAlreadyInGameException.class)
    public ResponseEntity<StatusResponse> handlePlayerAlreadyInGame(PlayerAlreadyInGameException ex) {
        StatusResponse response = new StatusResponse(null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StatusResponse> handleRuntimeException(RuntimeException ex) {
        StatusResponse response = new StatusResponse(null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
