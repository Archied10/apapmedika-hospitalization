package apap.ti.hospitalization2206826476.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import apap.ti.hospitalization2206826476.dto.request.AddRoomRequestDTO;
import apap.ti.hospitalization2206826476.dto.request.UpdateRoomRequestDTO;
import apap.ti.hospitalization2206826476.model.Room;
import apap.ti.hospitalization2206826476.service.RoomService;
import jakarta.validation.Valid;

@Controller
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/rooms")
    public String listRooms(Model model) {
        var listRoom = roomService.getAllRoom(false);
        model.addAttribute("listRoom", listRoom);
        return "viewall-room";
    }

    @GetMapping("/rooms/{roomId}")
    public String detailRoom(@PathVariable("roomId") String roomId, 
                            @RequestParam(value = "dateIn", required = false) Date dateIn,
                            @RequestParam(value = "dateOut", required = false) Date dateOut, 
                            Model model) {

        var room = roomService.getRoomById(roomId);

        model.addAttribute("room", room);

        return "view-room";
    }

    @GetMapping("/rooms/create")
    public String addRoomForm(Model model) {
        var roomDTO = new AddRoomRequestDTO();
        model.addAttribute("roomDTO", roomDTO);
        return "form-add-room";
    }

    @PostMapping("/rooms/create")
    public String addRoom(@Valid @ModelAttribute AddRoomRequestDTO roomDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("responseMessage", bindingResult.getFieldErrors()
            .stream()
            .map(error -> error.getDefaultMessage())
            .toList());
            return "response-room";
        }

        var room = new Room();
        room.setName(roomDTO.getName());
        room.setDescription(roomDTO.getDescription());
        room.setMaxCapacity(roomDTO.getMaxCapacity());
        room.setPricePerDay(roomDTO.getPricePerDay());

        roomService.addRoom(room);
        
        model.addAttribute("responseMessage",
                String.format("%s Room With the ID %s has been successfully created.", room.getName(), room.getId()));

        return "response-room";
    }

    @GetMapping("/rooms/{roomId}/update")
    public String updateRoomForm(@PathVariable("roomId") String roomId, Model model) {
        var room = roomService.getRoomById(roomId);

        var roomDTO = new UpdateRoomRequestDTO();
        roomDTO.setId(room.getId());
        roomDTO.setName(room.getName());
        roomDTO.setDescription(room.getDescription());
        roomDTO.setMaxCapacity(room.getMaxCapacity());
        roomDTO.setPricePerDay(room.getPricePerDay());
        model.addAttribute("roomDTO", roomDTO);
        return "form-update-room";
    }

    @PostMapping("/rooms/update")
    public String updateRoom(@Valid @ModelAttribute UpdateRoomRequestDTO roomDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("responseMessage", bindingResult.getFieldErrors()
            .stream()
            .map(error -> error.getDefaultMessage())
            .toList());
            return "response-room";
        }

        var roomFromDTO = new Room();
        roomFromDTO.setId(roomDTO.getId());
        roomFromDTO.setName(roomDTO.getName());
        roomFromDTO.setDescription(roomDTO.getDescription());
        roomFromDTO.setMaxCapacity(roomDTO.getMaxCapacity());
        roomFromDTO.setPricePerDay(roomDTO.getPricePerDay());

        var room = roomService.updateRoom(roomFromDTO);
        
        model.addAttribute("responseMessage",
                String.format("%s Room With the ID %s has been successfully updated.", room.getName(), room.getId()));

        return "response-room";
    }

    @GetMapping("/rooms/{roomId}/delete")
    public String deleteRoom(@PathVariable("roomId") String roomId, Model model) {
        var room = roomService.getRoomById(roomId);
        roomService.deleteRoom(room);

        model.addAttribute("responseMessage",
                String.format("%s Room With the ID %s has been successfully deleted.", room.getName(), room.getId()));

        return "response-room";
    }
}
