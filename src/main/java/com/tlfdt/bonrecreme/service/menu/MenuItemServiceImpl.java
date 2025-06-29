package com.tlfdt.bonrecreme.service.menu;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.menuitems.MenuItemRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.menuitems.MenuItemResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import com.tlfdt.bonrecreme.utils.menu.mapper.MenuItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "menuItems")
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    @Transactional("restaurantTransactionManager")
    @CacheEvict(cacheNames = "allMenuItems", allEntries = true)
    public MenuItemResponseDTO createMenuItem(MenuItemRequestDTO requestDTO) {
        // Business logic validation: Prevent duplicate names
        if (menuItemRepository.existsByName(requestDTO.getName())) {
            throw new CustomExceptionHandler("A menu item with the name '" + requestDTO.getName() + "' already exists.");
        }

        MenuItem menuItem = menuItemMapper.toNewEntity(requestDTO);
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        log.info("Created new menu item with ID: {}", savedMenuItem.getId());
        return menuItemMapper.toResponseDTO(savedMenuItem);
    }

    @Override
    @Cacheable(key = "#id")
    public MenuItemResponseDTO getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler("menuitems not found with ID: " + id));
        return menuItemMapper.toResponseDTO(menuItem);
    }

    @Override
    @Cacheable(cacheNames = "allMenuItems")
    public List<MenuItemResponseDTO> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        return menuItems.stream()
                .map(menuItemMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional("restaurantTransactionManager")
    @CachePut(key = "#id") // Updates the specific item cache
    @CacheEvict(cacheNames = "allMenuItems", allEntries = true) // Invalidates the paginated cache
    public MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO requestDTO) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler("menuitems not found with ID: " + id));

        // Use the mapper to apply updates
        menuItemMapper.updateEntityFromDTO(menuItem, requestDTO);

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        log.info("Updated menu item with ID: {}", updatedMenuItem.getId());
        return menuItemMapper.toResponseDTO(updatedMenuItem);
    }

    @Override
    @Transactional("restaurantTransactionManager")
    @CacheEvict(cacheNames = {"menuItems", "allMenuItems"}, allEntries = true) // Evict both caches
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new CustomExceptionHandler("menuitems not found with ID: " + id);
        }
        menuItemRepository.deleteById(id);
        log.info("Deleted menu item with ID: {}", id);
    }
}