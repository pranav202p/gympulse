package com.pranav.gympulse.common.error;



import com.pranav.gympulse.auth.exception.InvalidCredentialsException;
import com.pranav.gympulse.auth.exception.UserNotFoundException;
import com.pranav.gympulse.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex){

        ApiResponse<Void> response=new ApiResponse<>(
                false,
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex){

        ApiResponse<Void> response=new ApiResponse<>(
                false,
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}

