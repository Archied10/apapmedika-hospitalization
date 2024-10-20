package apap.ti.hospitalization2206826476.restcontroller;

import java.util.List;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apap.ti.hospitalization2206826476.restdto.response.BaseResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.ChartResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.ReservationResponseDTO;
import apap.ti.hospitalization2206826476.restservice.ReservationRestService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationRestController {
    private final ReservationRestService reservationRestService;

    public ReservationRestController(
        ReservationRestService reservationRestService
    ) {
        this.reservationRestService = reservationRestService;
    }

    @GetMapping("")
    public ResponseEntity<?> listReservations() {
        var baseResponseDTO = new BaseResponseDTO<List<ReservationResponseDTO>>();
        List<ReservationResponseDTO> reservations = reservationRestService.getAllReservation();

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(reservations);
        baseResponseDTO.setMessage(String.format("List Reservation berhasil ditemukan"));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<?> detailReservation(@PathVariable("Id") String Id) {
        var baseResponseDTO = new BaseResponseDTO<ReservationResponseDTO>();

        ReservationResponseDTO reservation = reservationRestService.getReservationById(Id);
        if (reservation == null) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(String.format("Data reservation tidak ditemukan"));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(reservation);
        baseResponseDTO.setMessage(String.format("Reservation dengan ID %s berhasil ditemukan", reservation.getId()));
        baseResponseDTO.setTimestamp(new Date());
        
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/chart")
    public ResponseEntity<?> getReservationChartData(
            @RequestParam("period") String period,
            @RequestParam("year") int year) {
        var baseResponseDTO = new BaseResponseDTO<List<ChartResponseDTO>>();
        List<ChartResponseDTO> chartData = reservationRestService.getReservationChartData(period, year);

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(chartData);
        baseResponseDTO.setMessage("Reservation chart data retrieved successfully");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }
}
