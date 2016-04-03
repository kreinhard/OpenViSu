package org.openvisu.repository;

import org.openvisu.domain.Monitor;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Monitor entity.
 */
public interface MonitorRepository extends JpaRepository<Monitor,Long> {

}
