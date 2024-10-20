package apap.ti.hospitalization2206826476.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import apap.ti.hospitalization2206826476.dto.request.AddReservationRequestDTO;
import apap.ti.hospitalization2206826476.model.Facility;
import apap.ti.hospitalization2206826476.model.Reservation;
import apap.ti.hospitalization2206826476.restdto.response.ReservationResponseDTO;

public interface ReservationService {
    List<ReservationResponseDTO> getAllReservationsFromRest() throws Exception;
    Reservation createReservation(AddReservationRequestDTO reservationDTO) throws Exception;
    ReservationResponseDTO getReservationByIdFromRest(String reservationId) throws Exception;
    Reservation getReservationById(String reservationId);
    Reservation updateFacilities(String reservationId, List<Facility> facilities);
    Reservation updateRoom(String reservationId, UUID nurseId, Date dateIn, Date dateOut, String roomId);
    void deleteReservation(Reservation reservation);
    List<Reservation> getAllReservation(boolean isDeleted);
    int countReservation();
}
