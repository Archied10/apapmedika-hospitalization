package apap.ti.hospitalization2206826476.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import apap.ti.hospitalization2206826476.model.Patient;
import apap.ti.hospitalization2206826476.model.Reservation;
import apap.ti.hospitalization2206826476.model.Room;
import apap.ti.hospitalization2206826476.repository.RoomDb;
import jakarta.persistence.EntityManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class RoomServiceImplTest {

    @Mock
    private RoomDb roomDb;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room room;
    private Reservation reservation1;
    private Reservation reservation2;
    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize Room, Reservations, and Patients for testing
        room = new Room();
        room.setId("RM0001");
        room.setMaxCapacity(2);
        room.setReservations(new ArrayList<>());

        patient1 = new Patient();
        patient1.setId(UUID.randomUUID());
        patient1.setName("John Doe");

        patient2 = new Patient();
        patient2.setId(UUID.randomUUID());
        patient2.setName("Jane Smith");

        reservation1 = new Reservation();
        reservation1.setDateIn(new Date(2023, Calendar.SEPTEMBER, 20));
        reservation1.setDateOut(new Date(2023, Calendar.SEPTEMBER, 25));
        reservation1.setPatient(patient1);

        reservation2 = new Reservation();
        reservation2.setDateIn(new Date(2023, Calendar.SEPTEMBER, 26));
        reservation2.setDateOut(new Date(2023, Calendar.SEPTEMBER, 30));
        reservation2.setPatient(patient2);

        room.getReservations().add(reservation1);
        room.getReservations().add(reservation2);
    }

    @Test
    public void testGetPatientInRoom_FilterByDate() {
        // Mock the room and reservation database call
        when(roomDb.findById("RM0001")).thenReturn(Optional.of(room));

        // Define the date range for filtering
        Date dateIn = new Date(2023, Calendar.SEPTEMBER, 20);
        Date dateOut = new Date(2023, Calendar.SEPTEMBER, 25);

        // Call the method under test
        List<Patient> patients = roomService.getPatientInRoom(room, dateIn, dateOut);

        // Verify the expected result
        assertNotNull(patients);
        assertEquals(1, patients.size()); // Only one patient should match
        assertEquals("John Doe", patients.get(0).getName()); // The matched patient should be John Doe

        // Verify that no more interactions happen with the mock
        verify(roomDb, times(0)).save(any(Room.class));
    }

    @Test
    public void testGetPatientInRoom_NoPatientsInDateRange() {
        // Mock the room and reservation database call
        when(roomDb.findById("RM0001")).thenReturn(Optional.of(room));

        // Define the date range where no reservations overlap
        Date dateIn = new Date(2023, Calendar.OCTOBER, 1);
        Date dateOut = new Date(2023, Calendar.OCTOBER, 5);

        // Call the method under test
        List<Patient> patients = roomService.getPatientInRoom(room, dateIn, dateOut);

        // Verify that no patients are returned
        assertNotNull(patients);
        assertTrue(patients.isEmpty());

        // Verify that no more interactions happen with the mock
        verify(roomDb, times(0)).save(any(Room.class));
    }
}
