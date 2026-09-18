package com.example.demo.controller;

import com.example.demo.model.Dentista;
import com.example.demo.repository.DentistaRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dentistas")
public class DentistaController {

    private final DentistaRepository repository;

    public DentistaController(DentistaRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Dentista criar(@RequestBody Dentista dentista) {
        return repository.save(dentista);
    }
}