package com.joaogabriel.notifyflow.infrastructure.persistence.repository;

import com.joaogabriel.notifyflow.infrastructure.persistence.entity.NotificationOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationOutboxJpaRepository extends JpaRepository<NotificationOutboxEntity, UUID> {
}
