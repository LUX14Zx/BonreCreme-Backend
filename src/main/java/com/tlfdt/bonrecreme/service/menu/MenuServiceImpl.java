package com.tlfdt.bonrecreme.service.menu;

import com.tlfdt.bonrecreme.api.v1.customer.dto.MenuResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;

    @Override
    @Cacheable("menuItems")
    public MenuResponseDTO getAllMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        return MenuResponseDTO.fromMenuItems(menuItems);
    }
}
