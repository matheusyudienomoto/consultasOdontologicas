package com.example.demo.controller;

import com.example.demo.model.Consulta;
import com.example.demo.repository.DentistaRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.ConsultaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;

    // As regras de agendamento ficam no service; os repositórios só populam os dropdowns
    public ConsultaController(ConsultaService consultaService,
                              PacienteRepository pacienteRepository,
                              DentistaRepository dentistaRepository) {
        this.consultaService = consultaService;
        this.pacienteRepository = pacienteRepository;
        this.dentistaRepository = dentistaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultas", consultaService.listarTodas());
        return "consultas";
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("consulta", new Consulta());
        // Envia as listas para popular os dropdowns no HTML
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("dentistas", dentistaRepository.findAll());
        return "consulta-form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Consulta consulta, RedirectAttributes redirectAttributes) {
        try {
            consultaService.salvar(consulta);
        } catch (RuntimeException e) {
            // Mostra na listagem o motivo de a consulta não ter sido salva
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro: " + e.getMessage());
            return "redirect:/consultas";
        }
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Consulta agendada com sucesso!");
        return "redirect:/consultas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Consulta consulta = consultaService.buscarPorId(id).orElse(null);
        if (consulta == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro: Consulta não encontrada.");
            return "redirect:/consultas";
        }
        model.addAttribute("consulta", consulta);
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("dentistas", dentistaRepository.findAll());
        return "consulta-form";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        consultaService.excluir(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Consulta cancelada com sucesso!");
        return "redirect:/consultas";
    }
}
