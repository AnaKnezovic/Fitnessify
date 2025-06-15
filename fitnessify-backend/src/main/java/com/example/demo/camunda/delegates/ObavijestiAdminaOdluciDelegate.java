package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("obavijestiAdminaOdluciDelegate")
public class ObavijestiAdminaOdluciDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        String status = (String) execution.getVariable("statusZahtjeva");
        System.out.println("[obavijestiAdminaOdluciDelegate] Admin obaviješten o ishodu: " + status);
        execution.setVariable("adminOdlukaObavijest", true);
    }
}
