package com.momagicbd.ConnectionCheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
