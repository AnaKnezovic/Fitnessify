package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("podsjetnikTreneruDelegate")
public class PodsjetnikTreneruDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        String trenerId = (String) execution.getVariable("trenerId");
        System.out.println("[podsjetnikTreneruDelegate] Podsjetnik poslan treneru " + trenerId);
        execution.setVariable("trenerPodsjetnik", true);
    }
}
