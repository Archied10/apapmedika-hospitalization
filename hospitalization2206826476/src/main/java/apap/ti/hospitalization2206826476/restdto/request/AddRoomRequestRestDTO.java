package apap.ti.hospitalization2206826476.restdto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddRoomRequestRestDTO {
    @NotBlank(message = "Room Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Positive(message = "Max Capacity has to be positive")
    private int maxCapacity;

    @Positive(message = "Price Per Day has to be positive")
    private double pricePerDay;
}
