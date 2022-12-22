package com.tomtom.scoop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthController {
    @GetMapping({"/", "/health"})
    @ResponseBody
    public String health() {
        return "Health Good";
    }
}
