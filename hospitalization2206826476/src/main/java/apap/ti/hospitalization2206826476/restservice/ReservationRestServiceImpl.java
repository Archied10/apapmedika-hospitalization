package apap.ti.hospitalization2206826476.restservice;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apap.ti.hospitalization2206826476.model.Reservation;
import apap.ti.hospitalization2206826476.repository.ReservationDb;
import apap.ti.hospitalization2206826476.restdto.response.ChartResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.FacilityResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.NurseResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.PatientResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.ReservationResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.RoomResponseDTO;
import jakarta.persistence.EntityManager;

@Service
@Transactional
public class ReservationRestServiceImpl implements ReservationRestService {
    private final ReservationDb reservationDb;
    private final EntityManager entityManager;

    public ReservationRestServiceImpl(
        ReservationDb reservationDb,
        EntityManager entityManager
    ) {
        this.reservationDb = reservationDb;
        this.entityManager = entityManager;
    }

    @Override
    public List<ReservationResponseDTO> getAllReservation() {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedReservationFilter");
        filter.setParameter("isDeleted", false);
        List<Reservation> reservations =  reservationDb.findAllByOrderByCreatedDateDesc();
        session.disableFilter("deletedReservationFilter");

        var reservationsResponseDTO = new ArrayList<ReservationResponseDTO>();
        reservations.forEach(reservation -> {
            var reservationResponseDTO = reservationToReservationResponseDTO(reservation);
            reservationsResponseDTO.add(reservationResponseDTO);
        });

        return reservationsResponseDTO;
    }

    @Override
    public ReservationResponseDTO getReservationById(String reservationId) {
        var reservation = reservationDb.findById(reservationId).orElse(null);

        if (reservation == null) {
            return null;
        }

        return reservationToReservationResponseDTO(reservation);
    }

    @Override
    public List<ChartResponseDTO> getReservationChartData(String period, int year) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedReservationFilter");
        filter.setParameter("isDeleted", false);
        List<Object[]> results;
        if ("monthly".equalsIgnoreCase(period)) {
            results = reservationDb.getMonthlyReservationCount(year);
        } else {
            results = reservationDb.getQuarterlyReservationCount(year);
        }

        List<ChartResponseDTO> chartData = new ArrayList<>();
        for (Object[] result : results) {
            String label = (String) result[0];
            Long count = (Long) result[1];
            chartData.add(new ChartResponseDTO(label, count));
        }
        session.disableFilter("deletedReservationFilter");
        return chartData;
    }

    private ReservationResponseDTO reservationToReservationResponseDTO(Reservation reservation) {
        var reservationResponseDTO = new ReservationResponseDTO();
        reservationResponseDTO.setId(reservation.getId());
        reservationResponseDTO.setDateIn(reservation.getDateIn());
        reservationResponseDTO.setDateOut(reservation.getDateOut());
        reservationResponseDTO.setTotalFee(reservation.getTotalFee());

        var patient = reservation.getPatient();
        PatientResponseDTO patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setId(patient.getId());
        patientResponseDTO.setNIK(patient.getNIK());
        patientResponseDTO.setName(patient.getName());
        patientResponseDTO.setEmail(patient.getEmail());
        patientResponseDTO.setBirthDate(patient.getBirthDate());
        patientResponseDTO.setGender(patient.isGender());
        patientResponseDTO.setCreatedDate(patient.getCreatedDate());
        patientResponseDTO.setUpdatedDate(patient.getUpdatedDate());
        reservationResponseDTO.setPatient(patientResponseDTO);

        var nurse = reservation.getNurse();
        NurseResponseDTO nurseResponseDTO = new NurseResponseDTO();
        nurseResponseDTO.setId(nurse.getId());
        nurseResponseDTO.setName(nurse.getName());
        nurseResponseDTO.setEmail(nurse.getEmail());
        nurseResponseDTO.setGender(nurse.isGender());
        nurseResponseDTO.setCreatedDate(nurse.getCreatedDate());
        nurseResponseDTO.setUpdatedDate(nurse.getUpdatedDate());
        reservationResponseDTO.setNurse(nurseResponseDTO);

        var room = reservation.getRoom();
        RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
        roomResponseDTO.setId(room.getId());
        roomResponseDTO.setName(room.getName());
        roomResponseDTO.setDescription(room.getDescription());
        roomResponseDTO.setMaxCapacity(room.getMaxCapacity());
        roomResponseDTO.setPricePerDay(room.getPricePerDay());
        roomResponseDTO.setCreatedDate(room.getCreatedDate());
        roomResponseDTO.setUpdatedDate(room.getUpdatedDate());
        reservationResponseDTO.setRoom(roomResponseDTO);
        
        if (reservation.getFacilities() != null) {
            var facilitiesResponseDTO = new ArrayList<FacilityResponseDTO>();
            reservation.getFacilities().forEach(facility -> {
                var facilityResponseDTO = new FacilityResponseDTO();
                facilityResponseDTO.setId(facility.getId());
                facilityResponseDTO.setName(facility.getName());
                facilityResponseDTO.setFee(facility.getFee());
                facilityResponseDTO.setCreatedDate(facility.getCreatedDate());
                facilityResponseDTO.setUpdatedDate(facility.getUpdatedDate());
                facilitiesResponseDTO.add(facilityResponseDTO);
            });
            reservationResponseDTO.setFacilities(facilitiesResponseDTO);
        }
        reservationResponseDTO.setCreatedDate(reservation.getCreatedDate());
        reservationResponseDTO.setUpdatedDate(reservation.getUpdatedDate());
        return reservationResponseDTO;
    }
}
