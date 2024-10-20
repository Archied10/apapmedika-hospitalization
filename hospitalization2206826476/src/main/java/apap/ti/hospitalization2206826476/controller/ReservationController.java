package apap.ti.hospitalization2206826476.controller;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import apap.ti.hospitalization2206826476.dto.request.AddReservationRequestDTO;
import apap.ti.hospitalization2206826476.dto.request.UpdateReservationRequestDTO;
import apap.ti.hospitalization2206826476.model.Facility;
import apap.ti.hospitalization2206826476.model.Nurse;
import apap.ti.hospitalization2206826476.model.Patient;
import apap.ti.hospitalization2206826476.model.Reservation;
import apap.ti.hospitalization2206826476.model.Room;
import apap.ti.hospitalization2206826476.restdto.response.FacilityResponseDTO;
import apap.ti.hospitalization2206826476.service.FacilityService;
import apap.ti.hospitalization2206826476.service.NurseService;
import apap.ti.hospitalization2206826476.service.PatientService;
import apap.ti.hospitalization2206826476.service.ReservationService;
import apap.ti.hospitalization2206826476.service.RoomService;
import jakarta.validation.Valid;

@Controller
public class ReservationController {
    private final PatientService patientService;
    private final NurseService nurseService;
    private final RoomService roomService;
    private final FacilityService facilityService;
    private final ReservationService reservationService;

    public ReservationController(
        PatientService patientService,
        NurseService nurseService,
        RoomService roomService,
        FacilityService facilityService,
        ReservationService reservationService
    ) {
        this.patientService = patientService;
        this.nurseService = nurseService;
        this.roomService = roomService;
        this.facilityService = facilityService;
        this.reservationService = reservationService;
    }

    enum Gender {
        Male,
        Female
    }

    @GetMapping("/")
    private String home(Model model) {
        model.addAttribute("reservationCount", reservationService.countReservation());
        model.addAttribute("roomCount", roomService.countRoom());
        model.addAttribute("patientCount", patientService.countPatient());
        return "home";
    }

    @GetMapping("/reservations")
    public String getListReservation(Model model) {
        try {
            var reservations = reservationService.getAllReservationsFromRest();
            model.addAttribute("reservations", reservations); 
            return "viewall-reservation";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "response-error-rest";
        }
    }

    @GetMapping("/reservations/chart")
    public String chart() {
        return "chart-reservation";
    }

    @GetMapping("/reservations/{reservationId}")
    public String detailRestReservation(@PathVariable("reservationId") String reservationId, Model model) { 
        try {
            var reservation = reservationService.getReservationByIdFromRest(reservationId);
            String facility = "";
            if (reservation.getFacilities() != null && !reservation.getFacilities().isEmpty()) {
                for (FacilityResponseDTO f: reservation.getFacilities()) {
                    facility += f.getName() + ", ";
                }
                facility = facility.substring(0, facility.length()-2);
            } else {
                facility = "No Facilities";
            }
            long daysStay = ChronoUnit.DAYS.between(reservation.getDateIn().toInstant(), reservation.getDateOut().toInstant())+1;
            double roomFee = reservation.getRoom().getPricePerDay() * daysStay;
            model.addAttribute("reservation", reservation);
            model.addAttribute("facility", facility);
            model.addAttribute("daysStay", daysStay);
            model.addAttribute("roomFee", roomFee);
            return "view-reservation";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "response-error-rest";
        }
    }

    @GetMapping("/reservations/create")
    public String searchPatient(Model model) {
        var reservationDTO = new AddReservationRequestDTO();
        model.addAttribute("reservationDTO", reservationDTO);
        return "search-patient";
    }
    
    @GetMapping("/reservations/create/step1")
    public String step1(@Valid @ModelAttribute AddReservationRequestDTO reservationDTO, Model model) {
        var patient = patientService.getPatientByNIK(reservationDTO.getNIK());
        if (patient == null) {
            reservationDTO.setGender(true);
            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("gender", Gender.values());
            return "form-add-patient";
        }

        reservationDTO.setPatientId(patient.getId());
        reservationDTO.setName(patient.getName());
        reservationDTO.setEmail(patient.getEmail());
        reservationDTO.setGender(patient.isGender());
        reservationDTO.setBirthDate(patient.getBirthDate());
        model.addAttribute("reservationDTO", reservationDTO);

        return "found-patient";
    }

    @GetMapping("/reservations/create/step2")
    public String step2(@Valid @ModelAttribute AddReservationRequestDTO reservationDTO, Model model) {
        if (reservationDTO.getName() == null || reservationDTO.getNIK() == null || reservationDTO.getEmail() == null
                || reservationDTO.getBirthDate() == null) {
            reservationDTO.setGender(true);
            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("gender", Gender.values());
            model.addAttribute("error", "Please fill in all required fields");
            return "form-add-patient";
        }
        var patient = patientService.getPatientByNIK(reservationDTO.getNIK());
        if (patient != null && reservationDTO.getPatientId() == null) {
            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("gender", Gender.values());
            model.addAttribute("error", "That NIK has already been registered");
            return "form-add-patient";
        }
        if (patient == null) {
            patient = new Patient();
            patient.setId(UUID.randomUUID());
            patient.setNIK(reservationDTO.getNIK());
            patient.setName(reservationDTO.getName());
            patient.setEmail(reservationDTO.getEmail());
            patient.setBirthDate(reservationDTO.getBirthDate());
            patient.setGender(reservationDTO.isGender());
            patientService.addPatient(patient);
        }
        model.addAttribute("reservationDTO", reservationDTO);
        model.addAttribute("nurses", nurseService.getAllNurses(false));
        return "find-room";
    }

    @PostMapping("/reservations/create/step2")
    public String step2Post(@Valid @ModelAttribute AddReservationRequestDTO reservationDTO, Model model) {
        if (reservationDTO.getDateIn() == null || reservationDTO.getDateOut() == null || reservationDTO.getNurseId() == null) {
            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("error", "Please fill in all required fields");
            model.addAttribute("nurses", nurseService.getAllNurses(false));
            return "find-room";
        }

        if (reservationDTO.getDateIn().after(reservationDTO.getDateOut())) {
            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("error", "Date In must be before or equal to Date Out");
            model.addAttribute("nurses", nurseService.getAllNurses(false));
            return "find-room";
        }

        List<Room> availableRooms = roomService.getAvailableRooms(reservationDTO.getDateIn(), reservationDTO.getDateOut());
        
        Map<String, Long> remainingCapacities = new HashMap<>();
        for (Room room : availableRooms) {
            long remainingCapacity = roomService.getRemainingCapacity(room, reservationDTO.getDateIn(), reservationDTO.getDateOut());
            remainingCapacities.put(room.getId(), remainingCapacity);
        }

        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("remainingCapacities", remainingCapacities);
        model.addAttribute("reservationDTO", reservationDTO);
        
        List<Nurse> nurses = nurseService.getAllNurses(false);
        model.addAttribute("nurses", nurses);

        return "find-room";
    }

    @GetMapping("/reservations/create/step3")
    public String step3(@Valid @ModelAttribute AddReservationRequestDTO reservationDTO, Model model) {
        Room room = roomService.getRoomById(reservationDTO.getRoomId());
        long remainingCapacity = roomService.getRemainingCapacity(room, reservationDTO.getDateIn(), reservationDTO.getDateOut());
        reservationDTO.setRoomSelected(room.getName() + " (Rp" + room.getPricePerDay() + "/Day, " + remainingCapacity + " Quota Available, Max " + room.getMaxCapacity() + " Pax)");
        model.addAttribute("reservationDTO", reservationDTO);
        model.addAttribute("facilitiesExisting", facilityService.getAllFacilities(false));
        return "add-facilities";
    }

    @PostMapping(value="/reservations/create", params={"addRow"})
    public String addRowStep3(@Valid @ModelAttribute AddReservationRequestDTO reservationDTO, Model model) {
        if (reservationDTO.getFacilities() == null || reservationDTO.getFacilities().isEmpty()) {
            reservationDTO.setFacilities(new ArrayList<>());
        }
        reservationDTO.getFacilities().add(new Facility());
        model.addAttribute("facilitiesExisting", facilityService.getAllFacilities(false));
        model.addAttribute("reservationDTO", reservationDTO);
        return "add-facilities";
    }

    @PostMapping(value="/reservations/create", params={"deleteRow"})
    public String deleteRowStep3(@Valid @ModelAttribute AddReservationRequestDTO reservationDTO, Model model, @RequestParam("deleteRow") int row) {
        reservationDTO.getFacilities().remove(row);
        model.addAttribute("facilitiesExisting", facilityService.getAllFacilities(false));
        model.addAttribute("reservationDTO", reservationDTO);
        return "add-facilities";
    }

    @PostMapping("/reservations/create")
    public String step3Post(@Valid @ModelAttribute AddReservationRequestDTO reservationDTO, BindingResult bindingResult, Model model) {
        model.addAttribute("reservationDTO", reservationDTO);
        if (bindingResult.hasErrors()) {
            model.addAttribute("facilities", facilityService.getAllFacilities(false));
            return "add-facilities";
        }

        try {
            Reservation reservation = reservationService.createReservation(reservationDTO);
            return "redirect:/reservations/" + reservation.getId() + "/feedback";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create reservation: " + e.getMessage());
            model.addAttribute("facilities", facilityService.getAllFacilities(false));
            return "add-facilities";
        }
    }

    @GetMapping("/reservations/{id}/feedback")
    public String reservationFeedback(@PathVariable("id") String reservationId, Model model) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        model.addAttribute("reservation", reservation);
        return "reservation-feedback";
    }

    @GetMapping("/reservations/{reservationId}/update-facilities")
    public String updateFacilitiesForm(@PathVariable("reservationId") String reservationId, Model model) {
        var reservation = reservationService.getReservationById(reservationId);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dateWithoutTime = cal.getTime();
        if (!dateWithoutTime.before(reservation.getDateOut())) {
            model.addAttribute("error", "You cannot update facilities after or equal to date out");
            try {
                var reservationFromRest = reservationService.getReservationByIdFromRest(reservationId);
                String facility = "";
                if (reservation.getFacilities() != null) {
                    for (FacilityResponseDTO f: reservationFromRest.getFacilities()) {
                        facility += f.getName() + ", ";
                    }
                    facility = facility.substring(0, facility.length()-2);
                }
                long daysStay = ChronoUnit.DAYS.between(reservationFromRest.getDateIn().toInstant(), reservationFromRest.getDateOut().toInstant())+1;
                double roomFee = reservation.getRoom().getPricePerDay() * daysStay;
                model.addAttribute("reservation", reservationFromRest);
                model.addAttribute("facility", facility);
                model.addAttribute("daysStay", daysStay);
                model.addAttribute("roomFee", roomFee);
                return "view-reservation";
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "response-error-rest";
            }
        }
        var reservationDTO = new UpdateReservationRequestDTO();
        reservationDTO.setReservationId(reservationId);
        reservationDTO.setFacilities(reservation.getFacilities());
        model.addAttribute("reservationDTO", reservationDTO);
        model.addAttribute("facilitiesExisting", facilityService.getAllFacilities(false));
        return "update-facilities";
    }

    @PostMapping("/reservations/{reservationId}/update-facilities")
    public String updateFacilities(@PathVariable("reservationId") String reservationId, @Valid @ModelAttribute UpdateReservationRequestDTO reservationDTO, Model model) {
        reservationService.updateFacilities(reservationId, reservationDTO.getFacilities());
        model.addAttribute("responseMessage",
                String.format("Facilities has been successfully updated"));
        return "response-reservation";
    }

    @PostMapping(value="/reservations/{reservationId}/update-facilities", params={"addRow"})
    public String addRowFacilities(@PathVariable("reservationId") String reservationId, @Valid @ModelAttribute UpdateReservationRequestDTO reservationDTO, Model model) {
        if (reservationDTO.getFacilities() == null || reservationDTO.getFacilities().isEmpty()) {
            reservationDTO.setFacilities(new ArrayList<>());
        }
        reservationDTO.getFacilities().add(new Facility());
        model.addAttribute("facilitiesExisting", facilityService.getAllFacilities(false));
        model.addAttribute("reservationDTO", reservationDTO);
        return "update-facilities";
    }

    @PostMapping(value="/reservations/{reservationId}/update-facilities", params={"deleteRow"})
    public String deleteRowFacilities(@PathVariable("reservationId") String reservationId, @Valid @ModelAttribute UpdateReservationRequestDTO reservationDTO, Model model, @RequestParam("deleteRow") int row) {
        reservationDTO.getFacilities().remove(row);
        model.addAttribute("facilitiesExisting", facilityService.getAllFacilities(false));
        model.addAttribute("reservationDTO", reservationDTO);
        return "update-facilities";
    }

    @GetMapping("/reservations/{reservationId}/update-room")
    public String updateRoom(@PathVariable("reservationId") String reservationId, Model model) {
        var reservation = reservationService.getReservationById(reservationId);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dateWithoutTime = cal.getTime();
        if (!dateWithoutTime.before(reservation.getDateIn())) {
            model.addAttribute("error", "You cannot update room after or equal to date in");
            try {
                var reservationFromRest = reservationService.getReservationByIdFromRest(reservationId);
                String facility = "";
                if (reservation.getFacilities() != null) {
                    for (FacilityResponseDTO f: reservationFromRest.getFacilities()) {
                        facility += f.getName() + ", ";
                    }
                    facility = facility.substring(0, facility.length()-2);
                }
                long daysStay = ChronoUnit.DAYS.between(reservationFromRest.getDateIn().toInstant(), reservationFromRest.getDateOut().toInstant())+1;
                double roomFee = reservation.getRoom().getPricePerDay() * daysStay;
                model.addAttribute("reservation", reservationFromRest);
                model.addAttribute("facility", facility);
                model.addAttribute("daysStay", daysStay);
                model.addAttribute("roomFee", roomFee);
                return "view-reservation";
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "response-error-rest";
            }
        }
        var reservationDTO = new UpdateReservationRequestDTO();
        reservationDTO.setReservationId(reservationId);
        reservationDTO.setNurseId(reservation.getNurse().getId());
        reservationDTO.setDateIn(reservation.getDateIn());
        reservationDTO.setDateOut(reservation.getDateOut());
        reservationDTO.setRoomId(reservation.getRoom().getId());

        List<Room> availableRooms = roomService.getAvailableRooms(reservationDTO.getDateIn(), reservationDTO.getDateOut());
        
        Map<String, Long> remainingCapacities = new HashMap<>();
        for (Room room : availableRooms) {
            long remainingCapacity = roomService.getRemainingCapacity(room, reservationDTO.getDateIn(), reservationDTO.getDateOut());
            remainingCapacities.put(room.getId(), remainingCapacity);
        }

        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("remainingCapacities", remainingCapacities);
        model.addAttribute("nurses", nurseService.getAllNurses(false));
        model.addAttribute("reservationDTO", reservationDTO);
        return "update-room";
    }

    @PostMapping("/reservations/{reservationId}/update-room/search")
    public String updateRoomSearch(@PathVariable("reservationId") String reservationId, @Valid @ModelAttribute UpdateReservationRequestDTO reservationDTO, Model model) {
        if (reservationDTO.getDateIn() == null || reservationDTO.getDateOut() == null || reservationDTO.getNurseId() == null) {
            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("error", "Please fill in all required fields");
            model.addAttribute("nurses", nurseService.getAllNurses(false));
            return "update-room";
        }

        if (reservationDTO.getDateIn().after(reservationDTO.getDateOut())) {
            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("error", "Date In must be before or equal to Date Out");
            model.addAttribute("nurses", nurseService.getAllNurses(false));
            return "update-room";
        }

        List<Room> availableRooms = roomService.getAvailableRooms(reservationDTO.getDateIn(), reservationDTO.getDateOut());
        
        Map<String, Long> remainingCapacities = new HashMap<>();
        for (Room room : availableRooms) {
            long remainingCapacity = roomService.getRemainingCapacity(room, reservationDTO.getDateIn(), reservationDTO.getDateOut());
            remainingCapacities.put(room.getId(), remainingCapacity);
        }

        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("remainingCapacities", remainingCapacities);
        model.addAttribute("nurses", nurseService.getAllNurses(false));
        model.addAttribute("reservationDTO", reservationDTO);
        return "update-room";
    }

    @PostMapping("/reservations/{reservationId}/update-room")
    public String updateRoom(@PathVariable("reservationId") String reservationId, @Valid @ModelAttribute UpdateReservationRequestDTO reservationDTO, Model model) {
        reservationService.updateRoom(reservationId, reservationDTO.getNurseId(), reservationDTO.getDateIn(), reservationDTO.getDateOut(), reservationDTO.getRoomId());
        model.addAttribute("responseMessage",
                String.format("Room has been successfully updated"));
        return "response-reservation";
    }

    @GetMapping("/reservations/{reservationId}/delete")
    public String deleteRoom(@PathVariable("reservationId") String reservationId, Model model) {
        var reservation = reservationService.getReservationById(reservationId);
        reservationService.deleteReservation(reservation);

        model.addAttribute("responseMessage",
                String.format("Berhasil menghapus reservasi %s", reservationId));

        return "response-reservation";
    }
}
