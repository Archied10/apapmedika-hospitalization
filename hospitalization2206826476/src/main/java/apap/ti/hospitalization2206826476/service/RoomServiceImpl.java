package apap.ti.hospitalization2206826476.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206826476.model.Patient;
import apap.ti.hospitalization2206826476.model.Reservation;
import apap.ti.hospitalization2206826476.model.Room;
import apap.ti.hospitalization2206826476.repository.RoomDb;
import jakarta.persistence.EntityManager;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomDb roomDb;
    private final EntityManager entityManager;

    public RoomServiceImpl(
        RoomDb roomDb,
        EntityManager entityManager
    ) {
        this.roomDb = roomDb;
        this.entityManager = entityManager;
    }

    @Override
    public Room addRoom(Room room) {
        int roomCount = (int) roomDb.count();
        String newRoomId = String.format("RM%04d", roomCount+1);
        room.setId(newRoomId);
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
        Filter filter = session.enableFilter("deletedRoomFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Room> rooms =  roomDb.findAllByOrderByIdAsc();
        session.disableFilter("deletedRoomFilter");
        return rooms;
    }

    @Override
    public List<Room> getAvailableRooms(Date dateIn, Date dateOut) {
        List<Room> allRooms = getAllRoom(false);
        return allRooms.stream()
                .filter(room -> isRoomAvailable(room, dateIn, dateOut))
                .collect(Collectors.toList());
    }

    private boolean isRoomAvailable(Room room, Date dateIn, Date dateOut) {
        long occupiedCapacity = room.getReservations().stream()
                .filter(reservation -> 
                    (!dateIn.after(reservation.getDateOut()) && !dateOut.before(reservation.getDateIn()))
                )
                .count();
        return occupiedCapacity < room.getMaxCapacity();
    }

    @Override
    public long getRemainingCapacity(Room room, Date dateIn, Date dateOut) {
        long occupiedCapacity = room.getReservations().stream()
                .filter(reservation -> 
                    (!dateIn.after(reservation.getDateOut()) && !dateOut.before(reservation.getDateIn()))
                )
                .count();
        return room.getMaxCapacity() - occupiedCapacity;
    }

    @Override
    public List<Patient> getPatientInRoom(Room room, Date dateIn, Date dateOut) {
        List<Reservation> reservations = room.getReservations().stream()
                .filter(reservation -> 
                    (!dateIn.after(reservation.getDateOut()) && !dateOut.before(reservation.getDateIn()))
                ).toList();
        List<Patient> patients = new ArrayList<>();
        if (!reservations.isEmpty()) {
            for (Reservation r: reservations) {
                patients.add(r.getPatient());
            }
        }
        
        return patients;
    }

    @Override
    public int countRoom() {
        return getAllRoom(false).size();
    }
}
