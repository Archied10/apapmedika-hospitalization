package apap.ti.hospitalization2206826476.service;

import java.util.List;

import apap.ti.hospitalization2206826476.model.Room;

public interface RoomService {
    Room addRoom(Room room);
    Room updateRoom(Room room);
    void deleteRoom(Room room);
    Room getRoomById(String roomId);
    List<Room> getAllRoom(boolean isDeleted);
}
