package apap.ti.hospitalization2206826476.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206826476.model.Room;
import apap.ti.hospitalization2206826476.repository.RoomDb;
import jakarta.persistence.EntityManager;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomDb roomDb;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Room addRoom(Room room) {
        int roomCount = (int) roomDb.count();
        String newRoomId = String.format("RM%04d", roomCount+1);
        room.setId(newRoomId);
        room.setReservations(new ArrayList<>());
        return roomDb.save(room);
    }

    @Override
    public Room updateRoom(Room room) {
        var getRoom = getRoomById(room.getId());
        if (getRoom != null) {
            getRoom.setName(room.getName());
            getRoom.setDescription(room.getDescription());
            getRoom.setMaxCapacity(room.getMaxCapacity());
            getRoom.setPricePerDay(room.getPricePerDay());
            roomDb.save(getRoom);
            return getRoom;
        }
        return null;
    }

    @Override
    public void deleteRoom(Room room) {
        roomDb.delete(room);
    }

    @Override
    public Room getRoomById(String roomId) {
        for (Room room: getAllRoom(false)) {
            if (room.getId().equals(roomId)) {
                return room;
            }
        }

        return null;
    }

    @Override
    public List<Room> getAllRoom(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Room> rooms =  roomDb.findAll();
        session.disableFilter("deletedProductFilter");
        return rooms;
    }
}
