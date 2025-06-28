package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link MenuItem} entities.
 * This interface provides a robust set of methods for data access, with a focus on
 * performance and security. It includes standard CRUD operations, custom derived queries,
 * and optimized queries using JPQL with EntityGraphs to prevent common performance pitfalls.
 *
 * @see MenuItem
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Finds a menu item by its unique name. This method is case-sensitive.
     *
     * @param name The name of the menu item to find.
     * @return An {@link Optional} containing the {@link MenuItem} if found.
     */
    Optional<MenuItem> findByName(String name);

    /**
     * Checks if a menu item with the given name exists. This is more performant
     * than fetching the entire entity with {@code findByName().isPresent()}.
     *
     * @param name The name to check for existence.
     * @return {@code true} if a menu item with the name exists, {@code false} otherwise.
     */
    boolean existsByName(String name);

    /**
     * Finds a menu item by its ID and eagerly fetches its associated {@code orderItems}.
     * This method uses an {@link EntityGraph} to prevent the N+1 query problem by
     * instructing JPA to join the {@code orderItems} table in the initial query.
     *
     * @param id The ID of the menu item to find.
     * @return An {@link Optional} containing the {@link MenuItem} with its order items initialized.
     */
    @EntityGraph(attributePaths = {"orderItems"})
    Optional<MenuItem> findInitializedById(Long id);

    /**
     * Finds all menu items with a price less than the specified amount.
     *
     * @param price The maximum price.
     * @return A list of menu items matching the criteria.
     */
    @Query("SELECT mi FROM MenuItem mi WHERE mi.price < :price")
    List<MenuItem> findByPriceLessThan(@Param("price") BigDecimal price);

    /**
     * Finds all menu items with a price within the specified range (inclusive).
     *
     * @param minPrice The minimum price.
     * @param maxPrice The maximum price.
     * @return A list of menu items within the price range.
     */
    @Query("SELECT mi FROM MenuItem mi WHERE mi.price BETWEEN :minPrice AND :maxPrice")
    List<MenuItem> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Finds all menu items whose names contain the given search term, ignoring case.
     * This method supports pagination and sorting via the {@link Pageable} parameter.
     *
     * @param name     The search term to find within menu item names.
     * @param pageable An object containing pagination and sorting information.
     * @return A {@link Page} of menu items matching the search criteria.
     */
    Page<MenuItem> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}