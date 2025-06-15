package com.example.demo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Klijent;
import com.example.demo.model.KlijentTrener;
import com.example.demo.model.KlijentTrenerId;
import com.example.demo.model.Trener;
import com.example.demo.repository.KlijentRepository;
import com.example.demo.repository.KlijentTrenerRepository;
import com.example.demo.repository.TrenerRepository;

@RestController
@RequestMapping("/api/klijent-trener")
public class KlijentTrenerController {
    @Autowired private RuntimeService runtimeService;
    @Autowired private KlijentTrenerRepository repo;
    @Autowired private KlijentRepository klijentRepo;
    @Autowired private TrenerRepository trenerRepo;

    // @PostMapping
    // public ResponseEntity<?> povezi(@RequestBody Map<String, Object> body) {
    //     Long klijentId = Long.valueOf(body.get("klijentId").toString());
    //     Long trenerId = Long.valueOf(body.get("trenerId").toString());
    //     Klijent klijent = klijentRepo.findById(klijentId).orElseThrow();
    //     Trener trener = trenerRepo.findById(trenerId).orElseThrow();

    //     KlijentTrenerId id = new KlijentTrenerId(klijentId, trenerId);
    //     KlijentTrener veza = new KlijentTrener();
    //     veza.setId(id);
    //     veza.setKlijent(klijent);
    //     veza.setTrener(trener);
    //     veza.setDatumPovezivanja(LocalDate.now());
    //     veza.setStatus("Čeka");
    //     repo.save(veza);
    //     return ResponseEntity.ok(veza);
    // }
    @PostMapping
    public ResponseEntity<?> povezi(@RequestBody Map<String, Object> body) {
        Long klijentId = Long.valueOf(body.get("klijentId").toString());
        Long trenerId = Long.valueOf(body.get("trenerId").toString());
        Klijent klijent = klijentRepo.findById(klijentId).orElseThrow();
        Trener trener = trenerRepo.findById(trenerId).orElseThrow();

        KlijentTrenerId id = new KlijentTrenerId(klijentId, trenerId);
        KlijentTrener veza = new KlijentTrener();
        veza.setId(id);
        veza.setKlijent(klijent);
        veza.setTrener(trener);
        veza.setDatumPovezivanja(LocalDate.now());
        veza.setStatus("Čeka");
        repo.save(veza);

        Map<String, Object> variables = new HashMap<>();
        variables.put("klijentId", klijentId.toString());
        variables.put("trenerId", trenerId.toString());
        runtimeService.startProcessInstanceByKey("Process_0ot2n3j", variables);

        return ResponseEntity.ok(veza);
    }


    @GetMapping("/moji")
    public List<KlijentTrener> moji(@RequestParam Long klijentId) {
        return repo.findByKlijentId(klijentId);
    }

    @GetMapping("/moji-klijenti")
    public List<KlijentTrener> mojiKlijenti(@RequestParam Long trenerId) {
        return repo.findByTrenerId(trenerId);
    }

    @PatchMapping("/potvrdi")
    public ResponseEntity<?> potvrdiVezu(@RequestBody Map<String, Object> body) {
        Long klijentId = Long.valueOf(body.get("klijentId").toString());
        Long trenerId = Long.valueOf(body.get("trenerId").toString());
        String status = body.get("status").toString(); // "Prihvaćeno" ili "Odbijeno"

        KlijentTrenerId id = new KlijentTrenerId(klijentId, trenerId);

        if ("Odbijeno".equals(status)) {
            repo.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            KlijentTrener veza = repo.findById(id).orElseThrow();
            veza.setStatus(status);
            repo.save(veza);
            return ResponseEntity.ok(veza);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> prekiniOdnos(@RequestParam Long klijentId, @RequestParam Long trenerId) {
        KlijentTrenerId id = new KlijentTrenerId(klijentId, trenerId);
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }



}
