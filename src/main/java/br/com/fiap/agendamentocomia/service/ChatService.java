package br.com.fiap.agendamentocomia.service;

import br.com.fiap.agendamentocomia.tool.AppointmentTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder builder,
                       AppointmentTool appointmentTool,
                       VectorStore vectorStore) {

        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();

        this.chatClient = builder
                .defaultSystem("""
                        Você é um assistente virtual de agendamentos de um salão de beleza.
                        Consulte sempre as regras do salão antes de agendar, cancelar ou remarcar.
                        Use a ferramenta getCurrentDateTime para validar as regras de antecedência.
                        Peça o nome completo do cliente, o serviço, a data e a hora antes de agendar.
                        Após agendar, confirme com os dados do agendamento e o ID.
                        """)
                .defaultTools(appointmentTool)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

    public String sendMessage(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}