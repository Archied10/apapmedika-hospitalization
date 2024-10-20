package apap.ti.hospitalization2206826476.restdto.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservationResponseDTO {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateIn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOut;
    private double totalFee;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PatientResponseDTO patient;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NurseResponseDTO nurse;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RoomResponseDTO room;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FacilityResponseDTO> facilities;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date updatedDate;
}
