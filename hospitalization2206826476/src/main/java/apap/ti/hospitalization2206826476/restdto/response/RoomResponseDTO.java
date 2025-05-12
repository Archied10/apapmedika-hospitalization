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
public class RoomResponseDTO {
    private String id;
    private String name;
    private String description;
    private int maxCapacity;
    private double pricePerDay;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ReservationResponseDTO> reservations;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date updatedDate;
}
