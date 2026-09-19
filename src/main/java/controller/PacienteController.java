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
    public String listar(@RequestParam(required = false) String busca,
                         @RequestParam(required = false, defaultValue = "ASC") String sort,
                         Model model) {

        org.springframework.data.domain.Sort sortOrder = sort.equalsIgnoreCase("DESC") ?
                org.springframework.data.domain.Sort.by("nome").descending() :
                org.springframework.data.domain.Sort.by("nome").ascending();

        if (busca != null && !busca.isEmpty()) {
            model.addAttribute("pacientes", repository.findByNomeContainingIgnoreCase(busca));
        } else {
            model.addAttribute("pacientes", repository.findAll(sortOrder));
        }

        // Prepara o botão para inverter a ordem no próximo clique
        model.addAttribute("nextSort", sort.equalsIgnoreCase("ASC") ? "DESC" : "ASC");
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