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
