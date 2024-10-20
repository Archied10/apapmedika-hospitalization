package apap.ti.hospitalization2206826476.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateRoomRequestDTO extends AddRoomRequestDTO {
    @NotBlank(message = "Room ID cannot be blank")
    private String id;
}
