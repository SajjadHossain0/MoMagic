package com.momagicbd.Repositories;

import com.momagicbd.Entities.ChargeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeConfigRepository extends JpaRepository<ChargeConfig, Long> {
    ChargeConfig findByOperator(String operator);
}
