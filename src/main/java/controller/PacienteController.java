package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Paciente criar(@RequestBody Paciente paciente) {
        return repository.save(paciente);
    }
}