package com.lostandfound.findora.service;

import com.lostandfound.findora.model.Item;

import java.util.List;

public interface MatchingService {

    List<Item> findMatches(Item newItem);
}
