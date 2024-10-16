package apap.ti.hospitalization2206826476.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti.hospitalization2206826476.model.Room;

@Repository
public interface RoomDb extends JpaRepository<Room, String> {
    List<Room> findAll();
    
}