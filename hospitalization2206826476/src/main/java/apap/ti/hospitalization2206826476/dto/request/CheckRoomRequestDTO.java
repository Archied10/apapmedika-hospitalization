package apap.ti.hospitalization2206826476.dto.request;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CheckRoomRequestDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateIn;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOut;
}
