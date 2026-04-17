package com.joaogabriel.notifyflow.infrastructure.persistence.repository;

import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import com.joaogabriel.notifyflow.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

    Page<NotificationEntity> findByTenantIdAndStatus(String tenantId, NotificationStatus status, Pageable pageable);

    Page<NotificationEntity> findByTenantId(String tenantId, Pageable pageable);

    Page<NotificationEntity> findByStatus(NotificationStatus status, Pageable pageable);
}
