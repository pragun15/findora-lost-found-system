package com.lostandfound.findora.service;

import com.lostandfound.findora.dto.ItemRequest;
import com.lostandfound.findora.model.Category;
import com.lostandfound.findora.model.Item;
import com.lostandfound.findora.model.ItemStatus;
import com.lostandfound.findora.model.User;
import com.lostandfound.findora.repository.CategoryRepository;
import com.lostandfound.findora.repository.ItemRepository;
import com.lostandfound.findora.repository.ItemSpecification;
import com.lostandfound.findora.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           AuditLogService auditLogService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public Item createItem(ItemRequest request) {
        User reporter = userRepository.findById(request.getReporterId())
                .orElseThrow(() -> new RuntimeException("Reporter not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Item item = new Item();
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(category);
        item.setColor(request.getColor());
        item.setKeywords(request.getKeywords());
        item.setStatus(ItemStatus.valueOf(request.getStatus()));
        item.setLocation(request.getLocation());
        item.setDateReported(request.getDateReported());
        item.setReporter(reporter);

        Item saved = itemRepository.save(item);

        auditLogService.log(reporter.getId(), "ITEM_CREATED", "items", saved.getId());

        return saved;
    }

    @Override
    public Page<Item> getAllItems(String keyword,
                                  String status,
                                  Integer categoryId,
                                  String color,
                                  String location,
                                  LocalDate dateFrom,
                                  LocalDate dateTo,
                                  Pageable pageable) {
        Specification<Item> spec = ItemSpecification.buildSpec(
                keyword, status, categoryId, color, location, dateFrom, dateTo
        );
        return itemRepository.findAll(spec, pageable);
    }

    @Override
    public Item getItemById(Integer id) {
        return itemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public Item updateItem(Integer id, ItemRequest request, Integer userId) {
        Item item = itemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getReporter().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to edit this item");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(category);
        item.setColor(request.getColor());
        item.setKeywords(request.getKeywords());
        item.setStatus(ItemStatus.valueOf(request.getStatus()));
        item.setLocation(request.getLocation());
        item.setDateReported(request.getDateReported());

        Item saved = itemRepository.save(item);

        auditLogService.log(userId, "ITEM_UPDATED", "items", saved.getId());

        return saved;
    }

    @Override
    public void softDeleteItem(Integer id, Integer userId) {
        Item item = itemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getReporter().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this item");
        }

        item.setIsDeleted(true);
        itemRepository.save(item);

        auditLogService.log(userId, "ITEM_DELETED", "items", id);
    }

    @Override
    public Page<Item> getItemsByUser(Integer userId, Pageable pageable) {
        return itemRepository.findByReporterIdAndIsDeletedFalse(userId, pageable);
    }
}
