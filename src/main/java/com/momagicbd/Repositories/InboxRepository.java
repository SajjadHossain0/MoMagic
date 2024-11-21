package com.momagicbd.Repositories;

import com.momagicbd.Entities.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, Long> {

    public List<Inbox> findByStatus(String status);
}
