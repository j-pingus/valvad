package com.valvad.repository;

import com.valvad.domain.Compatibility;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Compatibility entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompatibilityRepository extends JpaRepository<Compatibility, Long> {}
