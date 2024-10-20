package apap.ti.hospitalization2206826476.restservice;

import java.util.List;

import apap.ti.hospitalization2206826476.restdto.response.ChartResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.ReservationResponseDTO;

public interface ReservationRestService {
    List<ReservationResponseDTO> getAllReservation();
    ReservationResponseDTO getReservationById(String reservationId);
    List<ChartResponseDTO> getReservationChartData(String period, int year);
}
