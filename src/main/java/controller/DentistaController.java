package com.example.demo.controller;

import com.example.demo.model.Dentista;
import com.example.demo.repository.DentistaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dentistas")
public class DentistaController {

    private final DentistaRepository repository;

    public DentistaController(DentistaRepository repository) {
        this.repository = repository;
    }

    // LISTAR TODOS COM PESQUISA E ORDENAÇÃO
    @GetMapping
    public String listar(@RequestParam(required = false) String busca,
                         @RequestParam(required = false, defaultValue = "ASC") String sort,
                         Model model) {

        org.springframework.data.domain.Sort sortOrder = sort.equalsIgnoreCase("DESC") ?
                org.springframework.data.domain.Sort.by("nome").descending() :
                org.springframework.data.domain.Sort.by("nome").ascending();

        if (busca != null && !busca.isEmpty()) {
            // Precisamos criar esse método no DentistaRepository!
            model.addAttribute("dentistas", repository.findByNomeContainingIgnoreCase(busca));
        } else {
            model.addAttribute("dentistas", repository.findAll(sortOrder));
        }

        model.addAttribute("nextSort", sort.equalsIgnoreCase("ASC") ? "DESC" : "ASC");
        return "dentistas";
    }

    // ABRIR FORMULÁRIO (NOVO)
    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("dentista", new Dentista());
        return "dentista-form";
    }

    // SALVAR OU ATUALIZAR
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Dentista dentista, RedirectAttributes redirectAttributes) {
        repository.save(dentista);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Dentista salvo com sucesso!");
        return "redirect:/dentistas";
    }

    // ABRIR FORMULÁRIO (EDITAR)
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Dentista dentista = repository.findById(id).orElse(null);
        if (dentista == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro: Dentista não encontrado.");
            return "redirect:/dentistas";
        }
        model.addAttribute("dentista", dentista);
        return "dentista-form";
    }

    // EXCLUIR
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Dentista excluído com sucesso!");
        return "redirect:/dentistas";
    }
}