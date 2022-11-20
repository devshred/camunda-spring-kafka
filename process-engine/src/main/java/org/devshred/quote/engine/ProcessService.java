package org.devshred.quote.engine;

import static org.devshred.quote.engine.ProcessConstants.PROCESS_KEY_quote;
import static org.devshred.quote.engine.ProcessConstants.VAR_NAME_quote;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {
    private final RuntimeService runtimeService;

    public ProcessInstance startProcess(String correlationId, String locale, String type) {
        log.info("about to start process with correlationId: {}", correlationId);
        final ProcessInstance processInstance = runtimeService //
                .createProcessInstanceByKey(PROCESS_KEY_quote) //
                .businessKey(correlationId) //
                .setVariables(Map.of( //
                        ProcessConstants.VAR_NAME_locale, locale, //
                        ProcessConstants.VAR_NAME_quoteType, type)) //
                .execute();

        log.info("businessKey: {}", processInstance.getBusinessKey());
        log.info("processDefinitionId: {}", processInstance.getProcessDefinitionId());
        log.info("rootProcessInstanceId: {}", processInstance.getRootProcessInstanceId());

        return processInstance;
    }

    public void processQuoteResponse(String correlationId, String quote) {
        final Map<String, Object> variables = Map.of(VAR_NAME_quote, quote);
        try {
            final List<MessageCorrelationResult> messageCorrelationResults = //
                    runtimeService.createMessageCorrelation(ProcessConstants.MSG_NAME_QuoteResponse) //
                            .processInstanceBusinessKey(correlationId) //
                            .setVariables(variables) //
                            .correlateAllWithResult();

            if (messageCorrelationResults.isEmpty()) {
                log.error("failed to correlate");
            }
        } catch (ProcessEngineException e) {
            log.error("failed to process quoteResponse", e);
        }
    }
}
