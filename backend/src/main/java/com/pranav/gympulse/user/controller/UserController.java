package com.pranav.gympulse.user.controller;

import com.pranav.gympulse.common.dto.ApiResponse;
import com.pranav.gympulse.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserServiceImpl userService;

}
