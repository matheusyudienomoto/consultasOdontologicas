package com.example.demo.controller;

import com.example.demo.model.Consulta;
import com.example.demo.model.Dentista;
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
    private static final String[] CORES = {"#0e6a6a", "#b06f1c", "#7a4a93", "#3b6ea5", "#a8473d", "#4f7a3a", "#5f6b73"};

    private final ConsultaRepository consultaRepository;
    private final DentistaRepository dentistaRepository;

    public AgendaController(ConsultaRepository consultaRepository, DentistaRepository dentistaRepository) {
        this.consultaRepository = consultaRepository;
        this.dentistaRepository = dentistaRepository;
    }

    // ABRIR TELA DA AGENDA
    @GetMapping
    public String agenda(Model model) {
        List<Dentista> dentistas = dentistaRepository.findAll();
        Map<Long, String> coresDentistas = new HashMap<>();
        dentistas.forEach(d -> coresDentistas.put(d.getId(), corDoDentista(d.getId())));
        model.addAttribute("dentistas", dentistas);
        model.addAttribute("coresDentistas", coresDentistas);
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
        evento.put("cor", corDoDentista(consulta.getDentista().getId()));
        evento.put("dentista", consulta.getDentista().getNome());
        evento.put("status", consulta.getStatus() != null ? consulta.getStatus() : "AGENDADO");
        return evento;
    }

    // Cada dentista sempre recebe a mesma cor, na legenda e no calendário
    private String corDoDentista(Long dentistaId) {
        return CORES[(int) (dentistaId % CORES.length)];
    }
}
