package com.lostandfound.findora.service;

import com.lostandfound.findora.dto.ItemRequest;
import com.lostandfound.findora.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ItemService {

    Item createItem(ItemRequest request);

    Page<Item> getAllItems(String keyword,
                           String status,
                           Integer categoryId,
                           String color,
                           String location,
                           LocalDate dateFrom,
                           LocalDate dateTo,
                           Pageable pageable);

    Item getItemById(Integer id);

    Item updateItem(Integer id, ItemRequest request, Integer userId);

    void softDeleteItem(Integer id, Integer userId);

    Page<Item> getItemsByUser(Integer userId, Pageable pageable);
}
