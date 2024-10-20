package apap.ti.hospitalization2206826476.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti.hospitalization2206826476.model.Facility;

@Repository
public interface FacilityDb extends JpaRepository<Facility, UUID>{
    
}
