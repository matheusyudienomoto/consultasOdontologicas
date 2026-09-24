package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Retorna o usuário somente se login e senha estiverem corretos
    public Optional<Usuario> autenticar(String login, String senha) {
        return usuarioRepository.findByLogin(login)
                .filter(u -> u.getSenhaHash().equals(gerarHash(senha)));
    }

    // Cadastra um novo usuário, validando login repetido e tamanho da senha
    public Usuario cadastrar(String nome, String login, String senha, String confirmacaoSenha) {
        if (usuarioRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Este usuário já está em uso. Escolha outro.");
        }
        if (senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }
        if (!senha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("As senhas não conferem.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setLogin(login);
        usuario.setSenhaHash(gerarHash(senha));
        return usuarioRepository.save(usuario);
    }

    // Cria o usuário administrador na primeira execução do sistema
    public void criarAdminSeNaoExistir(String login, String senha) {
        if (usuarioRepository.findByLogin(login).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setLogin(login);
            admin.setSenhaHash(gerarHash(senha));
            usuarioRepository.save(admin);
        }
    }

    // Hash SHA-256 da senha
    private String gerarHash(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }
}
