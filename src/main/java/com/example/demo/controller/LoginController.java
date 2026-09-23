package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/agenda";
    }

    // ABRIR TELA DE LOGIN
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute(LoginInterceptor.USUARIO_SESSAO) != null) {
            return "redirect:/agenda";
        }
        return "login";
    }

    // VALIDAR LOGIN E SENHA
    @PostMapping("/login")
    public String entrar(@RequestParam String login,
                         @RequestParam String senha,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuario = usuarioService.autenticar(login, senha);
        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Usuário ou senha incorretos.");
            return "redirect:/login";
        }
        session.setAttribute(LoginInterceptor.USUARIO_SESSAO, usuario.get().getNome());
        return "redirect:/agenda";
    }

    // SAIR DO SISTEMA
    @GetMapping("/logout")
    public String sair(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Você saiu do sistema.");
        return "redirect:/login";
    }
}
