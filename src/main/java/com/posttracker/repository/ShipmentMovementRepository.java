package com.posttracker.repository;

import com.posttracker.model.ShipmentMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentMovementRepository extends JpaRepository<ShipmentMovement, Long> {
}
