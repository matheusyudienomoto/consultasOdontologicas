package com.example.demo.repository;

import com.example.demo.model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    java.util.List<Dentista> findByNomeContainingIgnoreCase(String nome);
}