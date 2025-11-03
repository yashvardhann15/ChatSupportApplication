package com.example.supportchatapplication.controller;

import com.example.supportchatapplication.dto.SearchResponse;
import com.example.supportchatapplication.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * GET /api/search?q=...
     * Searches customers and messages for the given query string.
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(@RequestParam("q") String query) {
        if (query == null || query.isBlank() || query.length() < 3) {
            return ResponseEntity.badRequest().body(new SearchResponse(List.of()));
        }
        
        SearchResponse results = searchService.searchAll(query);
        return ResponseEntity.ok(results);
    }
}