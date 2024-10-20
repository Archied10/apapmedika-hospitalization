package apap.ti.hospitalization2206826476.restservice;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206826476.model.Room;
import apap.ti.hospitalization2206826476.repository.RoomDb;
import apap.ti.hospitalization2206826476.restdto.request.AddRoomRequestRestDTO;
import apap.ti.hospitalization2206826476.restdto.response.ReservationResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.RoomResponseDTO;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoomRestServiceImpl implements RoomRestService {
    private final RoomDb roomDb;

    public RoomRestServiceImpl(
        RoomDb roomDb
    ) {
        this.roomDb = roomDb;
    }

    @Override
    public RoomResponseDTO addRoom(AddRoomRequestRestDTO roomDTO) {
        Room room = new Room();
        int roomCount = (int) roomDb.count();
        String newRoomId = String.format("RM%04d", roomCount+1);
        room.setId(newRoomId);
        room.setName(roomDTO.getName());
        room.setDescription(roomDTO.getDescription());
        room.setMaxCapacity(roomDTO.getMaxCapacity());
        room.setPricePerDay(roomDTO.getPricePerDay());
        Room newRoom = roomDb.save(room);
        return roomToRoomResponseDTO(newRoom);
    }

    private RoomResponseDTO roomToRoomResponseDTO(Room room) {
        var roomResponseDTO = new RoomResponseDTO();
        roomResponseDTO.setId(room.getId());
        roomResponseDTO.setName(room.getName());
        roomResponseDTO.setDescription(room.getDescription());
        roomResponseDTO.setMaxCapacity(room.getMaxCapacity());
        roomResponseDTO.setPricePerDay(room.getPricePerDay());

        if (room.getReservations() != null) {
            var reservations = new ArrayList<ReservationResponseDTO>();
            room.getReservations().forEach(reservation -> {
                var reservationResponseDTO = new ReservationResponseDTO();
                reservationResponseDTO.setId(reservation.getId());
                reservationResponseDTO.setDateIn(reservation.getDateIn());
                reservationResponseDTO.setDateOut(reservation.getDateOut());
                reservationResponseDTO.setTotalFee(reservation.getTotalFee());
                reservationResponseDTO.setCreatedDate(reservation.getCreatedDate());
                reservationResponseDTO.setUpdatedDate(reservation.getUpdatedDate());

                reservations.add(reservationResponseDTO);
            });
            roomResponseDTO.setReservations(reservations);
        }
        roomResponseDTO.setCreatedDate(room.getCreatedDate());
        roomResponseDTO.setUpdatedDate(room.getUpdatedDate());

        return roomResponseDTO;
    }
}
