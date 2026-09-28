package com.example.demo.repository;

import com.example.demo.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Consultas dentro de um período (usado pela agenda)
    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Consultas de um dentista específico dentro de um período
    List<Consulta> findByDentistaIdAndDataHoraBetween(Long dentistaId, LocalDateTime inicio, LocalDateTime fim);
}
