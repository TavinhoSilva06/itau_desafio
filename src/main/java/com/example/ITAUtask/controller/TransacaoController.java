package com.example.ITAUtask.controller;

import com.example.ITAUtask.dto.TransacaoRequest;
import com.example.ITAUtask.dto.TransacaoResponse;
import com.example.ITAUtask.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacao")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService service;

    @GetMapping
    public ResponseEntity<List<TransacaoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping("/criar")
    public ResponseEntity<Void> criar(
            @Valid @RequestBody TransacaoRequest request
    ) {

        service.registrar(request);

        return ResponseEntity.created(null).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> limpar() {

        service.limpar();

        return ResponseEntity.ok().build();
    }
}
