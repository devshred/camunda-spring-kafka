package org.devshred.quote.engine;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WriteQuoteAdapter implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        final Object quote = delegateExecution.getVariable(ProcessConstants.VAR_NAME_quote);
        if (quote == null) {
            log.info("Quote not found.");
        } else {
            log.info("Write quote: {}", quote);
        }
    }
}
