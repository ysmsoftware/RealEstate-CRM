package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.PropertyOptionsFlat;
import com.ysminfosolution.realestate.model.Floor;

public interface FloorRepository extends JpaRepository<Floor, UUID> {

  Set<Floor> findAllByWing_WingIdAndIsDeletedFalse(UUID wingId);

  Set<Floor> findAllByProject_ProjectIdAndIsDeletedFalse(UUID projectId);

  @Query(value = """
      SELECT
          f.property_type     AS propertyType,
          f.property          AS property,
          f.area              AS area,
          SUM(f.quantity)     AS quantity
      FROM floor f
      WHERE f.project_id = :projectId
        AND f.is_deleted = false
      GROUP BY f.property_type, f.property, f.area
      ORDER BY f.property_type, f.property, f.area
      """, nativeQuery = true)
  Set<PropertyOptionsFlat> getFlatPropertyOptions(UUID projectId);

}
