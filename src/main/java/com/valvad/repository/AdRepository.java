package com.valvad.repository;

import com.valvad.domain.Ad;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Ad entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    @Query("select ad from Ad ad where ad.publisher.login = ?#{principal.username}")
    List<Ad> findByPublisherIsCurrentUser();
}
