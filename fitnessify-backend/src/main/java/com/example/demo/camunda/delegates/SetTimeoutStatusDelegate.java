package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("setTimeoutStatusDelegate")
public class SetTimeoutStatusDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("statusZahtjeva", "TIMEOUT");
        System.out.println("[setTimeoutStatusDelegate] Status zahtjeva postavljen na TIMEOUT");
    }
}
