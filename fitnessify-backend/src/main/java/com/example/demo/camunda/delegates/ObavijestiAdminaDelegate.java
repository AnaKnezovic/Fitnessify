package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("obavijestiAdminaDelegate")
public class ObavijestiAdminaDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("[obavijestiAdminaDelegate] Admin je obaviješten o zahtjevu.");
        execution.setVariable("adminObavijesten", true);
    }
}
