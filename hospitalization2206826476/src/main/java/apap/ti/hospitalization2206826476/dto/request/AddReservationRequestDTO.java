package apap.ti.hospitalization2206826476.dto.request;

import java.util.Date;
import java.util.UUID;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import apap.ti.hospitalization2206826476.model.Facility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddReservationRequestDTO {
    private UUID patientId;
    private String NIK;
    private String name;
    private String email;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private boolean gender;

    private UUID nurseId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateIn;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOut;
    private String roomId;
    private String roomSelected;

    private List<Facility> facilities;
}
