package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("kreirajVezuDelegate")
public class KreirajVezuDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        String trenerId = (String) execution.getVariable("trenerId");
        String klijentId = (String) execution.getVariable("klijentId");
        System.out.println("[kreirajVezuDelegate] Veza stvorena između klijenta " + klijentId + " i trenera " + trenerId);
        execution.setVariable("vezaKreirana", true);
    }
}
