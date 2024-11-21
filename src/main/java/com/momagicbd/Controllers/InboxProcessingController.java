package com.momagicbd.Controllers;

import com.momagicbd.Services.InboxProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InboxProcessingController {
    private final InboxProcessingService inboxProcessingService;

    public InboxProcessingController(InboxProcessingService inboxProcessingService) {
        this.inboxProcessingService = inboxProcessingService;
    }

    @PostMapping("/inbox/process")
    public String processInbox() {
        inboxProcessingService.processInbox();
        return "Inbox processing initiated";
    }
}
