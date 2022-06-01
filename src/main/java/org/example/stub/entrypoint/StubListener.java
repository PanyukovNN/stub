package org.example.stub.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.stub.model.SmsResponse;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class StubListener {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = "DEV.QUEUE.1")
    public void onMessage(Message message) throws JMSException {
        String jmsCorrelationID = message.getJMSCorrelationID();
        String jmsMessageId = message.getJMSMessageID();
        String requestId = message.getStringProperty("RequestID");

        String body = message.getBody(String.class);
        log.info("Тело сообщения: " + body);

        log.info(message.toString());

        String headers = message.getStringProperty("headers");

        log.info("headers: " + headers);

        SmsResponse smsResponse = new SmsResponse();
        smsResponse.data = "Hello from stub";

        send(smsResponse, jmsCorrelationID, jmsMessageId, headers, requestId);
    }

    public void send(SmsResponse response, String correlationId, String messageId, String headers, String requestId) {
        String message = convertRequestToString(response);

        jmsTemplate.convertAndSend("DEV.QUEUE.2", message, postProcessor -> {
            postProcessor.setJMSCorrelationID(correlationId);
            postProcessor.setJMSMessageID(messageId);
            postProcessor.setStringProperty("headers", headers);
            postProcessor.setStringProperty("RequestID", requestId);

            return postProcessor;
        });
    }

    @SneakyThrows
    private String convertRequestToString(SmsResponse response) {
        return objectMapper.writeValueAsString(response);
    }
}
