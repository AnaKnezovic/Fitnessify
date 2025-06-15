package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("obavijestiTreneraDelegate")
public class ObavijestiTreneraDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        String trenerId = (String) execution.getVariable("trenerId");
        String klijentId = (String) execution.getVariable("klijentId");
        System.out.println("[obavijestiTreneraDelegate] Obavijest poslana treneru " + trenerId + " za klijenta " + klijentId);
        execution.setVariable("trenerObavijesten", true);
    }
}
