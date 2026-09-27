package com.example.demo.service;

import com.example.demo.model.Consulta;
import com.example.demo.model.Dentista;
import com.example.demo.model.Paciente;
import com.example.demo.repository.ConsultaRepository;
import com.example.demo.repository.DentistaRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Paciente paciente = buscarPaciente(pacienteId);
        Dentista dentista = buscarDentista(dentistaId);

        // 2. Regra de Negócio: Horário comercial (ex: 8h às 18h)
        validarHorario(dataHora);

        // 3. Salva a consulta
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setDentista(dentista);
        consulta.setDataHora(dataHora);
        consulta.setStatus("AGENDADO");

        return consultaRepository.save(consulta);
    }

    // Salva a consulta vinda do formulário (novo agendamento ou edição)
    public Consulta salvar(Consulta consulta) {
        Long pacienteId = consulta.getPaciente() != null ? consulta.getPaciente().getId() : null;
        Long dentistaId = consulta.getDentista() != null ? consulta.getDentista().getId() : null;

        consulta.setPaciente(buscarPaciente(pacienteId));
        consulta.setDentista(buscarDentista(dentistaId));
        validarHorario(consulta.getDataHora());

        if (consulta.getStatus() == null || consulta.getStatus().isBlank()) {
            consulta.setStatus("AGENDADO");
        }

        return consultaRepository.save(consulta);
    }

    // Método bônus para listar todas as consultas depois
    public List<Consulta> listarTodas() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> buscarPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public void excluir(Long id) {
        consultaRepository.deleteById(id);
    }

    private Paciente buscarPaciente(Long pacienteId) {
        if (pacienteId == null) {
            throw new RuntimeException("Paciente não encontrado!");
        }
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado!"));
    }

    private Dentista buscarDentista(Long dentistaId) {
        if (dentistaId == null) {
            throw new RuntimeException("Dentista não encontrado!");
        }
        return dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado!"));
    }

    // Atendimento de segunda a sábado, das 8h às 18h
    private void validarHorario(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new RuntimeException("Informe a data e o horário da consulta.");
        }
        if (dataHora.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RuntimeException("Não há atendimento aos domingos.");
        }
        if (dataHora.getHour() < 8 || dataHora.getHour() > 18) {
            throw new RuntimeException("A consulta deve ser agendada em horário comercial (8h às 18h).");
        }
    }
}
