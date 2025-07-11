package wonbin.scheduler.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FirstPageController {

    @CrossOrigin(origins ="https://localhost:3000")
    @GetMapping("/hello")
    public String first() {
        log.info("첫 페이지 접근");
        return "Hello Spring boot!";
    }
}