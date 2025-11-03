package com.example.supportchatapplication.dto;

import java.util.List;

/**
 * The main wrapper for our search results.
 */
public record SearchResponse(
    List<SearchResultDTO> results
) {}