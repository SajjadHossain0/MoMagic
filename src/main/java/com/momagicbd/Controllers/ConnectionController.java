package com.momagicbd.Controllers;

import com.momagicbd.Services.ConnectionService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @GetMapping("/test")
    public String test() {
        return "test successfully";
    }

    @GetMapping("/check-connection")
    public String checkConnection() {
        connectionService.checkConnection();
        return "check connection successfully";
    }

}
