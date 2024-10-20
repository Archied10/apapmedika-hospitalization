package apap.ti.hospitalization2206826476.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti.hospitalization2206826476.model.Nurse;

@Repository
public interface NurseDb extends JpaRepository<Nurse, UUID>{
    
}
