package apap.ti.hospitalization2206826476.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservationController {
    @GetMapping("/")
    private String home() {
        return "home";
    }
}
