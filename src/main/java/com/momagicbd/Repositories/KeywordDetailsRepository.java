package com.momagicbd.Repositories;

import com.momagicbd.Entities.KeywordDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordDetailsRepository extends JpaRepository<KeywordDetails, Long> {

    public boolean existsByKeyword(String keyword);
}
