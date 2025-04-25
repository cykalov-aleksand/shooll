package ru.hogwarts.shooll.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/port")
public class InfoControllerPort {
    @Value("${server.port}")
    private String port;
    @GetMapping
    @Operation(summary = "Получаем номер порта используемый при работе")
    public String getPortInfo() {
        return port;
    }
}
