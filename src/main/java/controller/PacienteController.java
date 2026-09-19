package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    // LISTAR TODOS E PESQUISA
    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        // Se o usuário digitou algo na busca, a gente tenta filtrar (vamos criar esse método no Repository depois)
        // Se não, lista todos normalmente
        if (busca != null && !busca.isEmpty()) {
            // Deixei comentado porque você ainda precisa criar o findByNomeContaining no Repository!
            // model.addAttribute("pacientes", repository.findByNomeContaining(busca));
        } else {
            model.addAttribute("pacientes", repository.findAll());
        }
        return "pacientes";
    }

    // ABRIR FORMULÁRIO DE CADASTRO
    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "paciente-form"; // Retorna o arquivo paciente-form.html
    }

    // SALVAR OU ATUALIZAR (Vindo do formulário)
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Paciente paciente, RedirectAttributes redirectAttributes) {
        repository.save(paciente);
        // Mensagem de sucesso via Bootstrap Alert (Requisito do professor!)
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Paciente salvo com sucesso!");
        return "redirect:/pacientes";
    }

    // ABRIR FORMULÁRIO PARA EDIÇÃO
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Paciente paciente = repository.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro: Paciente não encontrado.");
            return "redirect:/pacientes";
        }
        model.addAttribute("paciente", paciente);
        return "paciente-form";
    }

    // EXCLUIR
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Paciente excluído com sucesso!");
        return "redirect:/pacientes";
    }
}