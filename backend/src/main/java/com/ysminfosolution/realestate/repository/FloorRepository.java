package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.PropertyOptionsFlat;
import com.ysminfosolution.realestate.model.Floor;

public interface FloorRepository extends JpaRepository<Floor, UUID> {

  Set<Floor> findAllByWing_Id(UUID wingId);

  Set<Floor> findAllByProject_Id(UUID projectId);

  @Query("""
      SELECT
          f.propertyType AS propertyType,
          f.property AS property,
          f.area AS area,
          SUM(f.quantity) AS quantity
      FROM Floor f
      WHERE f.project.id = :projectId
        AND f.deleted = false
      GROUP BY f.propertyType, f.property, f.area
      ORDER BY f.propertyType, f.property, f.area
      """)
  Set<PropertyOptionsFlat> getFlatPropertyOptions(UUID projectId);

}
