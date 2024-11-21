package com.momagicbd.Repositories;

import com.momagicbd.Entities.ChargeSuccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeSuccessRepository extends JpaRepository<ChargeSuccess, Long>{

}
