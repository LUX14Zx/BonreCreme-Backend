package com.tlfdt.bonrecreme.repository.user;

import com.tlfdt.bonrecreme.model.user.Role;
import com.tlfdt.bonrecreme.model.user.User;
import com.tlfdt.bonrecreme.model.user.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @NonNull
    Optional<Role> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    List<Role> findByName(RoleName name);
    
    List<Role> findByUser(User user);
    
    boolean existsByNameAndUser(RoleName name, User user);
}