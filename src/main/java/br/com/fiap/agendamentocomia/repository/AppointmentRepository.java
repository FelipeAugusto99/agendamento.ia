package br.com.fiap.agendamentocomia.repository;

import br.com.fiap.agendamentocomia.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
