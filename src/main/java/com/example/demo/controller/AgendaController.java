package com.example.demo.controller;

import com.example.demo.model.Consulta;
import com.example.demo.repository.ConsultaRepository;
import com.example.demo.repository.DentistaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/agenda")
public class AgendaController {

    // Cores usadas para diferenciar os dentistas no calendário
    private static final String[] CORES = {"#0d6efd", "#198754", "#6f42c1", "#fd7e14", "#d63384", "#20c997", "#6c757d"};

    private final ConsultaRepository consultaRepository;
    private final DentistaRepository dentistaRepository;

    public AgendaController(ConsultaRepository consultaRepository, DentistaRepository dentistaRepository) {
        this.consultaRepository = consultaRepository;
        this.dentistaRepository = dentistaRepository;
    }

    // ABRIR TELA DA AGENDA
    @GetMapping
    public String agenda(Model model) {
        model.addAttribute("dentistas", dentistaRepository.findAll());
        return "agenda";
    }

    // CONSULTAS DO PERÍODO EM JSON (lido pelo calendário)
    @GetMapping("/eventos")
    @ResponseBody
    public List<Map<String, Object>> eventos(@RequestParam String start,
                                             @RequestParam String end,
                                             @RequestParam(required = false) Long dentistaId) {
        // O calendário envia datas como "2026-09-28T00:00:00-03:00"; usamos só data e hora
        LocalDateTime inicio = LocalDateTime.parse(start.substring(0, 19));
        LocalDateTime fim = LocalDateTime.parse(end.substring(0, 19));

        List<Consulta> consultas = dentistaId != null
                ? consultaRepository.findByDentistaIdAndDataHoraBetween(dentistaId, inicio, fim)
                : consultaRepository.findByDataHoraBetween(inicio, fim);

        return consultas.stream().map(this::converterParaEvento).toList();
    }

    private Map<String, Object> converterParaEvento(Consulta consulta) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("id", consulta.getId());
        evento.put("title", consulta.getPaciente().getNome());
        evento.put("start", consulta.getDataHora().toString());
        // Cada consulta ocupa 1 hora na agenda
        evento.put("end", consulta.getDataHora().plusHours(1).toString());
        evento.put("color", CORES[(int) (consulta.getDentista().getId() % CORES.length)]);
        evento.put("dentista", consulta.getDentista().getNome());
        evento.put("status", consulta.getStatus() != null ? consulta.getStatus() : "AGENDADO");
        return evento;
    }
}
