package com.tlfdt.bonrecreme.service.menu;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.MenuItem.MenuItemRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.MenuItem.MenuItemResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;

import com.tlfdt.bonrecreme.model.restaurant.MenuItem;

import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Override
    @Transactional("restaurantTransactionManager")
    @CacheEvict(value = "menuItems", allEntries = true)
    public MenuItemResponseDTO createMenuItem(MenuItemRequestDTO requestDTO) {

        MenuItem menuItem = new MenuItem();
        menuItem.setName(requestDTO.getName());
        menuItem.setDescription(requestDTO.getDescription());
        menuItem.setPrice(requestDTO.getPrice());

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return MenuItemResponseDTO.fromMenuItem(savedMenuItem);
    }

    @Override
    @Cacheable(value = "menuItems", key = "#id")
    public MenuItemResponseDTO getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler("MenuItem not found"));
        return MenuItemResponseDTO.fromMenuItem(menuItem);
    }

    @Override
    public List<MenuItemResponseDTO> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(MenuItemResponseDTO::fromMenuItem)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional("restaurantTransactionManager")
    @CachePut(value = "menuItems", key = "#id")
    @CacheEvict(value = "menuItems", allEntries = true)
    public MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO requestDTO) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler("MenuItem not found"));


        menuItem.setName(requestDTO.getName());
        menuItem.setDescription(requestDTO.getDescription());
        menuItem.setPrice(requestDTO.getPrice());


        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return MenuItemResponseDTO.fromMenuItem(updatedMenuItem);
    }

    @Override
    @Transactional("restaurantTransactionManager")
    @CacheEvict(value = "menuItems", allEntries = true)
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new CustomExceptionHandler("MenuItem not found");
        }
        menuItemRepository.deleteById(id);
    }
}
