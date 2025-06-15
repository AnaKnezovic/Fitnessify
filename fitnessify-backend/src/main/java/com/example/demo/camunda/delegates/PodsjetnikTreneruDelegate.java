package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("podsjetnikTreneruDelegate")
public class PodsjetnikTreneruDelegate implements JavaDelegate {

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
        //Long trenerId = (Long) execution.getVariable("trenerId");
        System.out.println("[podsjetnikTreneruDelegate] Podsjetnik poslan treneru " + trenerId);
        execution.setVariable("trenerPodsjetnik", true);
    }
}
