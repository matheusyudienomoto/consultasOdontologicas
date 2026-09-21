package com.example.demo.controller;

import com.example.demo.model.Consulta;
import com.example.demo.service.ConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity<Consulta> agendar(@RequestParam Long pacienteId,
                                            @RequestParam Long dentistaId,
                                            @RequestParam String dataHora) {
        // Converte a string de data enviada na requisição para o formato do Java
        LocalDateTime data = LocalDateTime.parse(dataHora);
        Consulta novaConsulta = consultaService.agendarConsulta(pacienteId, dentistaId, data);
        return ResponseEntity.ok(novaConsulta);
    }

    @GetMapping
    public ResponseEntity<List<Consulta>> listar() {
        return ResponseEntity.ok(consultaService.listarTodas());
    }
}