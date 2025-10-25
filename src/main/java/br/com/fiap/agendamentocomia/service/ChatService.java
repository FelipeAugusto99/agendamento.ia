package br.com.fiap.agendamentocomia.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final AppointmentService appointmentService;

    public ChatService(ChatClient.Builder builder, AppointmentService appointmentService) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();
        this.chatClient = builder
                .defaultSystem("""
                        Você é um assistente virtual especializado em agendamentos de consultas de salão de beleza.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.appointmentService = appointmentService;
    }

    public String sendMessage(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
