package com.momagicbd.Repositories;

import com.momagicbd.Entities.ChargeFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeFailureRepository extends JpaRepository<ChargeFailure, Long> {

}
