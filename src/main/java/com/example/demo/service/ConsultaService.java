package com.example.demo.service;

import com.example.demo.model.Consulta;
import com.example.demo.model.Dentista;
import com.example.demo.model.Paciente;
import com.example.demo.repository.ConsultaRepository;
import com.example.demo.repository.DentistaRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;

    // Injeção de dependências via construtor (melhor prática no Spring)
    public ConsultaService(ConsultaRepository consultaRepository,
                           PacienteRepository pacienteRepository,
                           DentistaRepository dentistaRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.dentistaRepository = dentistaRepository;
    }

    public Consulta agendarConsulta(Long pacienteId, Long dentistaId, LocalDateTime dataHora) {

        // 1. Verifica se paciente e dentista existem no banco de dados
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado!"));

        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado!"));

        // 2. Regra de Negócio: Horário comercial (ex: 8h às 18h)
        if (dataHora.getHour() < 8 || dataHora.getHour() > 18) {
            throw new RuntimeException("A consulta deve ser agendada em horário comercial (8h às 18h).");
        }

        // 3. Salva a consulta
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setDentista(dentista);
        consulta.setDataHora(dataHora);
        consulta.setStatus("AGENDADO");

        return consultaRepository.save(consulta);
    }

    // Método bônus para listar todas as consultas depois
    public List<Consulta> listarTodas() {
        return consultaRepository.findAll();
    }
}