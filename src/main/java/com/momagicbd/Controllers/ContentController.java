package com.momagicbd.Controllers;

import com.momagicbd.Services.ContentService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/retrieve-content")
    public String retrieveContent() {
        contentService.contentRetrieve();
        return "Content retrieved and saved successfully";
    }
}
