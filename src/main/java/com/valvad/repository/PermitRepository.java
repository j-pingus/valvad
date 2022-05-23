package com.valvad.repository;

import com.valvad.domain.Permit;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Permit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PermitRepository extends JpaRepository<Permit, Long> {
    @Query("select permit from Permit permit where permit.user.login = ?#{principal.username}")
    List<Permit> findByUserIsCurrentUser();
}
