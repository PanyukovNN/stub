package org.example.stub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StubController {

    @GetMapping("/")
    public ResponseEntity<String> post() {
        return ResponseEntity.badRequest()
                .body("");
    }
}
