package apap.ti.hospitalization2206826476.service;

import java.util.UUID;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.time.temporal.ChronoUnit;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import apap.ti.hospitalization2206826476.dto.request.AddReservationRequestDTO;
import apap.ti.hospitalization2206826476.model.Facility;
import apap.ti.hospitalization2206826476.model.Nurse;
import apap.ti.hospitalization2206826476.model.Patient;
import apap.ti.hospitalization2206826476.model.Reservation;
import apap.ti.hospitalization2206826476.model.Room;
import apap.ti.hospitalization2206826476.repository.ReservationDb;
import apap.ti.hospitalization2206826476.restdto.response.BaseResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.ReservationResponseDTO;
import jakarta.persistence.EntityManager;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationDb reservationDb;
    private final PatientService patientService;
    private final RoomService roomService;
    private final NurseService nurseService;
    private final FacilityService facilityService;
    private final EntityManager entityManager;
    private final WebClient webClient;

    public ReservationServiceImpl(
        ReservationDb reservationDb,
        PatientService patientService,
        RoomService roomService,
        NurseService nurseService,
        FacilityService facilityService,
        EntityManager entityManager,
        WebClient.Builder webClientBuilder
    ) {
        this.reservationDb = reservationDb;
        this.patientService = patientService;
        this.roomService = roomService;
        this.nurseService = nurseService;
        this.facilityService = facilityService;
        this.entityManager = entityManager;
        this.webClient = webClientBuilder
                            .baseUrl("http://localhost:8080/api")
                            .build();
    }

    @Override
    public Reservation createReservation(AddReservationRequestDTO reservationDTO) throws Exception {
        Patient patient = patientService.getPatientByNIK(reservationDTO.getNIK());

        Room room = roomService.getRoomById(reservationDTO.getRoomId());
        Nurse nurse = nurseService.getNurseById(reservationDTO.getNurseId());

        Reservation reservation = new Reservation();
        reservation.setId(generateReservationId(reservationDTO));
        reservation.setDateIn(reservationDTO.getDateIn());
        reservation.setDateOut(reservationDTO.getDateOut());
        reservation.setPatient(patient);
        reservation.setNurse(nurse);
        reservation.setRoom(room);
        reservation.setFacilities(reservationDTO.getFacilities());
        reservation.setTotalFee(calculateTotalFee(reservation));

        return reservationDb.save(reservation);
    }

    @Override
    public Reservation getReservationById(String reservationId) {
        return reservationDb.findById(reservationId).orElse(null);
    }

    private String generateReservationId(AddReservationRequestDTO reservationDTO) {
        StringBuilder idBuilder = new StringBuilder("RES");

        // Calculate days difference
        long daysDiff = ChronoUnit.DAYS.between(reservationDTO.getDateIn().toInstant(), reservationDTO.getDateOut().toInstant());
        String daysDiffStr = String.format("%02d", daysDiff % 100);
        idBuilder.append(daysDiffStr);

        // Get day of week
        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String dayOfWeek = days[cal.get(Calendar.DAY_OF_WEEK) - 1];
        idBuilder.append(dayOfWeek);

        // Get last 4 characters of NIK
        String nik = reservationDTO.getNIK();
        String lastFourNik = String.format("%04d", Integer.parseInt(nik.substring(Math.max(0, nik.length() - 4))));
        idBuilder.append(lastFourNik);
        // Get total number of reservations (including soft deleted ones)
        long totalReservations = reservationDb.count();
        String reservationCount = String.format("%04d", (totalReservations + 1) % 10000);
        idBuilder.append(reservationCount);

        String generatedId = idBuilder.toString();

        return generatedId;
    }

    @Override
    public List<ReservationResponseDTO> getAllReservationsFromRest() throws Exception {
        var response = webClient
            .get()
            .uri("/reservations")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<ReservationResponseDTO>>>() {})
            .block();

        if (response == null) {
            throw new Exception("Failed consume API getAllPekerja");
        }

        if (response.getStatus() != 200) {
            throw new Exception(response.getMessage());
        }

        return response.getData();
    }

    @Override
    public ReservationResponseDTO getReservationByIdFromRest(String reservationId) throws Exception {
        var response = webClient
            .get()
            .uri("/reservations/" + reservationId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ReservationResponseDTO>>() {})
            .block();

        if (response == null) {
            throw new Exception("Failed consume API getPekerjaById");
        }

        if (response.getStatus() != 200) {
            throw new Exception(response.getMessage());
        }

        return response.getData();
    }

    @Override
    public Reservation updateFacilities(String reservationId, List<Facility> facilities) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation != null) {
            reservation.setFacilities(facilities);
            reservation.setTotalFee(calculateTotalFee(reservation));
            reservationDb.save(reservation);

            return reservation;
        }
        return null;
    }

    @Override
    public Reservation updateRoom(String reservationId, UUID nurseId, Date dateIn, Date dateOut, String roomId) {
        Reservation reservation = getReservationById(reservationId);
        Nurse nurse = nurseService.getNurseById(nurseId);
        Room room = roomService.getRoomById(roomId);
        if (reservation != null) {
            reservation.setNurse(nurse);
            reservation.setRoom(room);
            reservation.setDateIn(dateIn);
            reservation.setDateOut(dateOut);
            reservation.setTotalFee(calculateTotalFee(reservation));
            reservationDb.save(reservation);

            return reservation;
        }
        return null;
    }

    private double calculateTotalFee(Reservation reservation) {
        long daysStay = ChronoUnit.DAYS.between(reservation.getDateIn().toInstant(), reservation.getDateOut().toInstant())+1;
        double roomFee = reservation.getRoom().getPricePerDay() * daysStay;

        double facilitiesFee = 0;
        if (reservation.getFacilities() != null) {
            for (Facility facility: reservation.getFacilities()) {
                facilitiesFee += facilityService.getFacilityById(facility.getId()).getFee();
            }
        }
        return roomFee + facilitiesFee;
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        reservationDb.delete(reservation);
    }

    @Override
    public List<Reservation> getAllReservation(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedReservationFilter");
        filter.setParameter("isDeleted", false);
        List<Reservation> reservations =  reservationDb.findAll();
        session.disableFilter("deletedReservationFilter");
        return reservations;
    }

    @Override
    public int countReservation() {
        return getAllReservation(false).size();
    }
}
