package com.joaogabriel.notifyflow.infrastructure.persistence.repository;

import com.joaogabriel.notifyflow.infrastructure.persistence.entity.NotificationOutboxEntity;
import com.joaogabriel.notifyflow.infrastructure.persistence.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationOutboxJpaRepository extends JpaRepository<NotificationOutboxEntity, UUID> {

    List<NotificationOutboxEntity> findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
