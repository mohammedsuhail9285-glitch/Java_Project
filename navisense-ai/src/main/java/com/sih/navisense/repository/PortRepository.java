package com.sih.navisense.repository;

import com.sih.navisense.entity.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortRepository extends JpaRepository<Port, Long> {
    Optional<Port> findByNameIgnoreCase(String name);
    Optional<Port> findByPortCodeIgnoreCase(String portCode);
    List<Port> findByPortTypeIgnoreCase(String portType);
    List<Port> findByCountryIgnoreCase(String country);
}
