package apap.ti.hospitalization2206826476.restservice;

import apap.ti.hospitalization2206826476.restdto.request.AddRoomRequestRestDTO;
import apap.ti.hospitalization2206826476.restdto.response.RoomResponseDTO;

public interface RoomRestService {
    RoomResponseDTO addRoom(AddRoomRequestRestDTO roomDTO);
}
