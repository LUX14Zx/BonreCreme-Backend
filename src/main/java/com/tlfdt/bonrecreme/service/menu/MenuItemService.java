package com.tlfdt.bonrecreme.service.menu;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.MenuItem.MenuItemRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.MenuItem.MenuItemResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * A service interface for managing the business logic of menu items.
 * <p>
 * This service provides a comprehensive API for creating, retrieving, updating,
 * and deleting menu items, including support for pagination to handle large menus.
 */
public interface MenuItemService {

    /**
     * Creates a new menu item based on the provided data.
     *
     * @param requestDTO A DTO containing the details for the new menu item.
     * @return A {@link MenuItemResponseDTO} representing the newly created item.
     * @throws CustomExceptionHandler if a menu item with the same name already exists.
     */
    MenuItemResponseDTO createMenuItem(MenuItemRequestDTO requestDTO) throws CustomExceptionHandler;

    /**
     * Retrieves a specific menu item by its unique identifier.
     *
     * @param id The unique ID of the menu item to retrieve.
     * @return A {@link MenuItemResponseDTO} representing the found item.
     * @throws CustomExceptionHandler if no menu item with the given ID is found.
     */
    MenuItemResponseDTO getMenuItemById(Long id) throws CustomExceptionHandler;

    /**
     * Retrieves a paginated list of all menu items.
     * <p>
     * Using pagination is crucial for performance when dealing with a large number of
     * menu items, preventing the system from loading all items into memory at once.
     *
     * @return A {@link List} of {@link MenuItemResponseDTO}s.
     */
    List<MenuItemResponseDTO> getAllMenuItems();

    /**
     * Updates an existing menu item with new data.
     *
     * @param id         The unique ID of the menu item to update.
     * @param requestDTO A DTO containing the updated details.
     * @return A {@link MenuItemResponseDTO} representing the updated item.
     * @throws CustomExceptionHandler if the menu item to update is not found.
     */
    MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO requestDTO) throws CustomExceptionHandler;

    /**
     * Deletes a menu item by its unique identifier.
     *
     * @param id The unique ID of the menu item to delete.
     * @throws CustomExceptionHandler if the menu item to delete is not found.
     */
    void deleteMenuItem(Long id) throws CustomExceptionHandler;
}