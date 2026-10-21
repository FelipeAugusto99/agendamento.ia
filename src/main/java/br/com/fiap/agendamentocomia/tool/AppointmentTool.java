package br.com.fiap.agendamentocomia.tool;

import br.com.fiap.agendamentocomia.model.Appointment;
import br.com.fiap.agendamentocomia.model.AppointmentStatus;
import br.com.fiap.agendamentocomia.service.AppointmentService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AppointmentTool {

    private final AppointmentService appointmentService;

    public AppointmentTool(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Tool(description = "Retorna a data e hora atuais, para validar as regras de antecedência")
    public String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }

    @Tool(description = "Cria um agendamento. Só chame depois de validar as regras")
    public String createAppointment(
            @ToolParam(description = "Nome completo do cliente") String clientName,
            @ToolParam(description = "Nome exato do serviço") String service,
            @ToolParam(description = "Data no formato dd/MM/yyyy") String date,
            @ToolParam(description = "Hora no formato HH:mm") String time) {

        if (!appointmentService.isAvailable(date, time)) {
            return "Horário indisponível.";
        }
        var a = appointmentService.createAppointment(
                new Appointment(null, clientName, service, date, time, AppointmentStatus.SCHEDULED));
        return "Agendado! ID: " + a.getId();
    }

    @Tool(description = "Cancela um agendamento pelo ID")
    public String cancelAppointment(@ToolParam(description = "ID do agendamento") Long id) {
        var a = appointmentService.getById(id);
        if (a == null) return "Agendamento não encontrado.";
        a.setStatus(AppointmentStatus.CANCELED);
        appointmentService.updateAppointment(a, id);
        return "Agendamento " + id + " cancelado.";
    }

    @Tool(description = "Remarca um agendamento pelo ID para nova data e hora")
    public String rescheduleAppointment(
            @ToolParam(description = "ID do agendamento") Long id,
            @ToolParam(description = "Nova data dd/MM/yyyy") String newDate,
            @ToolParam(description = "Nova hora HH:mm") String newTime) {

        var a = appointmentService.getById(id);
        if (a == null) return "Agendamento não encontrado.";
        if (!appointmentService.isAvailable(newDate, newTime)) return "Horário indisponível.";
        a.setAppointmentDate(newDate);
        a.setAppointmentTime(newTime);
        appointmentService.updateAppointment(a, id);
        return "Agendamento " + id + " remarcado.";
    }
}