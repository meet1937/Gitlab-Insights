package com.md.gi.scheduler.repository;

import com.md.gi.scheduler.enums.Status;
import com.md.gi.scheduler.model.Integration;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for Integeration Credentials */
@Repository
public interface IntegrationRepository extends JpaRepository<Integration, UUID> {

  Integration findByIdAndStatus(UUID id, Status status);

  List<Integration> findByStatus(Status status);
}
