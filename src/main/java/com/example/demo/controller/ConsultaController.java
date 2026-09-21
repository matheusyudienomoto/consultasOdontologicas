package com.example.demo.controller;

import com.example.demo.model.Consulta;
import com.example.demo.repository.ConsultaRepository;
import com.example.demo.repository.DentistaRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;

    // Injeção de dependência dos três repositórios necessários
    public ConsultaController(ConsultaRepository consultaRepository,
                              PacienteRepository pacienteRepository,
                              DentistaRepository dentistaRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.dentistaRepository = dentistaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultas", consultaRepository.findAll());
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
        consultaRepository.save(consulta);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Consulta agendada com sucesso!");
        return "redirect:/consultas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Consulta consulta = consultaRepository.findById(id).orElse(null);
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
        consultaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Consulta cancelada com sucesso!");
        return "redirect:/consultas";
    }
}