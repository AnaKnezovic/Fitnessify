package com.example.demo.controller;

import com.example.demo.model.Trener;
import com.example.demo.repository.TrenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treneri")
public class TreneriController {
    @Autowired
    private TrenerRepository trenerRepo;

    @GetMapping
    public List<Trener> getAll() {
        return trenerRepo.findAll();
    }
}
