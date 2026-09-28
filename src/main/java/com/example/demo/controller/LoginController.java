package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    private static final int LEMBRAR_SEGUNDOS = 7 * 24 * 60 * 60;

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
                         @RequestParam(required = false) String lembrar,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuario = usuarioService.autenticar(login, senha);
        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Usuário ou senha incorretos.");
            return "redirect:/login";
        }
        // Gera um novo id de sessão ao entrar, evitando reaproveitar uma sessão anterior
        HttpSession session = request.getSession();
        request.changeSessionId();
        session.setAttribute(LoginInterceptor.USUARIO_SESSAO, usuario.get().getNome());

        // "Lembrar de mim": a sessão dura 7 dias e o cookie sobrevive ao fechar o navegador
        if (lembrar != null) {
            session.setMaxInactiveInterval(LEMBRAR_SEGUNDOS);
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
            cookie.setHttpOnly(true);
            cookie.setMaxAge(LEMBRAR_SEGUNDOS);
            response.addCookie(cookie);
        }
        return "redirect:/agenda";
    }

    // ABRIR TELA DE CADASTRO
    @GetMapping("/cadastro")
    public String mostrarCadastro() {
        return "cadastro";
    }

    // CRIAR NOVA CONTA
    @PostMapping("/cadastro")
    public String cadastrar(@RequestParam String nome,
                            @RequestParam String login,
                            @RequestParam String senha,
                            @RequestParam String confirmacaoSenha,
                            RedirectAttributes redirectAttributes) {
        try {
            usuarioService.cadastrar(nome.trim(), login.trim(), senha, confirmacaoSenha);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("login", login);
            return "redirect:/cadastro";
        }
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Conta criada com sucesso! Faça login para continuar.");
        return "redirect:/login";
    }

    // SAIR DO SISTEMA
    @GetMapping("/logout")
    public String sair(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Você saiu do sistema.");
        return "redirect:/login";
    }
}
