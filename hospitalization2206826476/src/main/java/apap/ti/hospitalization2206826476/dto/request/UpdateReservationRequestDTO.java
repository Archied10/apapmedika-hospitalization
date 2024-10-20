package apap.ti.hospitalization2206826476.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateReservationRequestDTO extends AddReservationRequestDTO {
    private String reservationId;
}
