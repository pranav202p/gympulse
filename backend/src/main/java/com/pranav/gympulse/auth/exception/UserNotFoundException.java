package com.pranav.gympulse.auth.exception;

import com.pranav.gympulse.common.error.GlobalExceptionHandler;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){
        super(message);
    }
}
