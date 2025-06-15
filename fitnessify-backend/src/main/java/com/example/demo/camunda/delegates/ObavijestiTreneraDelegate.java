package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("obavijestiTreneraDelegate")
public class ObavijestiTreneraDelegate implements JavaDelegate {

    private Long getLongVar(DelegateExecution execution, String varName) {
        Object val = execution.getVariable(varName);
        if (val instanceof String) {
            return Long.valueOf((String) val);
        } else if (val instanceof Number) {
            return ((Number) val).longValue();
        } else {
        return null;
        }
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long trenerId = getLongVar(execution, "trenerId");
        Long klijentId = getLongVar(execution, "klijentId");
        //Long trenerId = (Long) execution.getVariable("trenerId");
        //Long klijentId = (Long) execution.getVariable("klijentId");
        System.out.println("[obavijestiTreneraDelegate] Obavijest poslana treneru " + trenerId + " za klijenta " + klijentId);
        execution.setVariable("trenerObavijesten", true);
    }
}
