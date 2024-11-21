package com.momagicbd.Controllers;

import com.momagicbd.Services.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/retrive-content")
    public String retriveContent() {
        contentService.contentRetrive();
        return "Content retrieved and saved successfully";
    }
}
