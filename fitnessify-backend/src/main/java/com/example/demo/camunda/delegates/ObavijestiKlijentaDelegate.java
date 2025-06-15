package com.example.demo.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.KlijentTrenerId;
import com.example.demo.repository.KlijentTrenerRepository;

@Component("obavijestiKlijentaDelegate")
public class ObavijestiKlijentaDelegate implements JavaDelegate {

    @Autowired
    private KlijentTrenerRepository klijentTrenerRepository;

    @Override
    public void execute(DelegateExecution execution) {
        Long klijentId = Long.valueOf(execution.getVariable("klijentId").toString());
        Long trenerId = Long.valueOf(execution.getVariable("trenerId").toString());
        String status = (String) execution.getVariable("statusZahtjeva");

        // Ažuriraj status veze u bazi
        KlijentTrenerId id = new KlijentTrenerId(klijentId, trenerId);
        klijentTrenerRepository.findById(id).ifPresent(veza -> {
            veza.setStatus(status);
            klijentTrenerRepository.save(veza);
        });

        System.out.println("[obavijestiKlijentaDelegate] Klijent " + klijentId + " obaviješten o odluci: " + status);
        execution.setVariable("klijentObavijesten", true);
    }
}
