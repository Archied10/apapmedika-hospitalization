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
        reservation1.setDateIn(new Date(2024, Calendar.OCTOBER, 20));
        reservation1.setDateOut(new Date(2024, Calendar.OCTOBER, 25));
        reservation1.setPatient(patient1);

        reservation2 = new Reservation();
        reservation2.setDateIn(new Date(2024, Calendar.OCTOBER, 26));
        reservation2.setDateOut(new Date(2024, Calendar.OCTOBER, 30));
        reservation2.setPatient(patient2);

        room.getReservations().add(reservation1);
        room.getReservations().add(reservation2);
    }

    /*
     * Mengetes jika terdapat satu pasien pada range tanggal yang diberikan
     */
    @Test
    public void testGetPatientInRoom_FilterByDate() {
        when(roomDb.findById("RM0001")).thenReturn(Optional.of(room));

        Date dateIn = new Date(2024, Calendar.OCTOBER, 20);
        Date dateOut = new Date(2024, Calendar.OCTOBER, 25);

        List<Patient> patients = roomService.getPatientInRoom(room, dateIn, dateOut);

        assertNotNull(patients);
        assertEquals(1, patients.size());
        assertEquals("John Doe", patients.get(0).getName()); 

        verify(roomDb, times(0)).save(any(Room.class));
    }

    /*
     * Mengetes jika terdapat banyak pasien pada range tanggal yang diberikan
     */
    @Test
    public void testGetPatientInRoom_MultiplePatientsInDateRange() {
        when(roomDb.findById("RM0001")).thenReturn(Optional.of(room));

        Date dateIn = new Date(2024, Calendar.OCTOBER, 22);
        Date dateOut = new Date(2024, Calendar.OCTOBER, 26);

        List<Patient> patients = roomService.getPatientInRoom(room, dateIn, dateOut);

        assertNotNull(patients);
        assertEquals(2, patients.size());

        verify(roomDb, times(0)).save(any(Room.class));
    }

    /*
     * Mengetes jika tidak ada pasien pada range tanggal yang diberikan
     */
    @Test
    public void testGetPatientInRoom_NoPatientsInDateRange() {
        when(roomDb.findById("RM0001")).thenReturn(Optional.of(room));

        Date dateIn = new Date(2024, Calendar.OCTOBER, 1);
        Date dateOut = new Date(2024, Calendar.OCTOBER, 3);

        List<Patient> patients = roomService.getPatientInRoom(room, dateIn, dateOut);

        assertNotNull(patients);
        assertTrue(patients.isEmpty());

        verify(roomDb, times(0)).save(any(Room.class));
    }
}
