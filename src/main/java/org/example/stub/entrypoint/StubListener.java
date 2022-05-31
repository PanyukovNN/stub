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

        String headers = message.getStringProperty("headers");

        SmsResponse smsResponse = new SmsResponse();
        smsResponse.data = "Hello from stub";

        send(smsResponse, jmsCorrelationID, jmsMessageId, headers);
    }

    public void send(SmsResponse response, String correlationId, String messageId, String headers) {
        String message = convertRequestToString(response);

        jmsTemplate.convertAndSend("DEV.QUEUE.2", message, postProcessor -> {
            postProcessor.setJMSCorrelationID(correlationId);
            postProcessor.setJMSMessageID(messageId);
            postProcessor.setStringProperty("headers", headers);

            return postProcessor;
        });
    }

    @SneakyThrows
    private String convertRequestToString(SmsResponse response) {
        return objectMapper.writeValueAsString(response);
    }
}
