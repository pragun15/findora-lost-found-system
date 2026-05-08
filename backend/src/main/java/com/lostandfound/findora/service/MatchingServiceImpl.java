package com.lostandfound.findora.service;

import com.lostandfound.findora.model.Item;
import com.lostandfound.findora.model.ItemStatus;
import com.lostandfound.findora.repository.ItemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MatchingServiceImpl implements MatchingService {

    private final ItemRepository itemRepository;

    public MatchingServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> findMatches(Item newItem) {
        if (newItem == null || newItem.getCategory() == null) {
            return List.of();
        }

        String keyword = firstKeyword(newItem.getKeywords());

        List<Item> candidates = itemRepository.findPotentialMatches(
                ItemStatus.FOUND,
                newItem.getCategory().getId(),
                newItem.getColor(),
                newItem.getLocation(),
                keyword,
                PageRequest.of(0, 20) // grab a small set first, then score
        );

        List<ScoredItem> scored = new ArrayList<>();
        for (Item candidate : candidates) {
            int score = calculateScore(newItem, candidate);
            if (score > 0) {
                scored.add(new ScoredItem(candidate, score));
            }
        }

        scored.sort(Comparator.comparingInt(ScoredItem::score).reversed());

        List<Item> top5 = new ArrayList<>();
        for (int i = 0; i < Math.min(5, scored.size()); i++) {
            top5.add(scored.get(i).item());
        }

        return top5;
    }

    private int calculateScore(Item input, Item candidate) {
        int score = 0;

        if (sameCategory(input, candidate)) {
            score += 3;
        }

        if (sameColor(input, candidate)) {
            score += 2;
        }

        if (keywordMatch(input.getKeywords(), candidate.getKeywords())) {
            score += 2;
        }

        if (locationMatch(input.getLocation(), candidate.getLocation())) {
            score += 1;
        }

        if (dateNear(input.getDateReported(), candidate.getDateReported())) {
            score += 1;
        }

        return score;
    }

    private boolean sameCategory(Item a, Item b) {
        return a.getCategory() != null && b.getCategory() != null
                && a.getCategory().getId().equals(b.getCategory().getId());
    }

    private boolean sameColor(Item a, Item b) {
        return a.getColor() != null && b.getColor() != null
                && a.getColor().equalsIgnoreCase(b.getColor());
    }

    private boolean keywordMatch(String sourceKeywords, String targetKeywords) {
        if (sourceKeywords == null || targetKeywords == null) return false;
        String[] source = sourceKeywords.toLowerCase().split(",");
        String target = targetKeywords.toLowerCase();
        for (String k : source) {
            String trimmed = k.trim();
            if (!trimmed.isEmpty() && target.contains(trimmed)) {
                return true;
            }
        }
        return false;
    }

    private boolean locationMatch(String source, String target) {
        if (source == null || target == null) return false;
        return target.toLowerCase().contains(source.toLowerCase());
    }

    private boolean dateNear(LocalDate a, LocalDate b) {
        if (a == null || b == null) return false;
        return !b.isBefore(a.minusDays(7)) && !b.isAfter(a.plusDays(7));
    }

    private String firstKeyword(String keywords) {
        if (keywords == null || keywords.isBlank()) return null;
        String[] parts = keywords.split(",");
        return parts.length > 0 ? parts[0].trim() : null;
    }

    private record ScoredItem(Item item, int score) {}
}
