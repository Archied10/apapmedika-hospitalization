package apap.ti.hospitalization2206826476.restcontroller;

import java.util.List;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apap.ti.hospitalization2206826476.restdto.request.AddRoomRequestRestDTO;
import apap.ti.hospitalization2206826476.restdto.response.BaseResponseDTO;
import apap.ti.hospitalization2206826476.restdto.response.RoomResponseDTO;
import apap.ti.hospitalization2206826476.restservice.RoomRestService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rooms")
public class RoomRestController {
    private final RoomRestService roomRestService;

    public RoomRestController(
        RoomRestService roomRestService
    ) {
        this.roomRestService = roomRestService;
    }

    @PostMapping("")
    public ResponseEntity<?> addRoom(@Valid @RequestBody AddRoomRequestRestDTO roomDTO,
                                       BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<RoomResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getDefaultMessage() + "; ";
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        RoomResponseDTO room = roomRestService.addRoom(roomDTO);
        if (room == null) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(String.format("Data room tidak ditemukan"));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.CREATED.value());
        baseResponseDTO.setData(room);
        baseResponseDTO.setMessage(String.format("Room dengan ID %s berhasil ditambahkan", room.getId()));
        baseResponseDTO.setTimestamp(new Date());

        return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
    }
}
