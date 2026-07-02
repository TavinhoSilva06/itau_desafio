package com.example.ITAUtask.controller;

import com.example.ITAUtask.dto.EstatisticaIntervaloHistoricoResponse;
import com.example.ITAUtask.dto.EstatisticaIntervaloRequest;
import com.example.ITAUtask.dto.EstatisticaIntervaloResponse;
import com.example.ITAUtask.dto.EstatisticaResponse;
import com.example.ITAUtask.service.EstatisticaConfiguracaoService;
import com.example.ITAUtask.service.EstatisticaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EstatisticaController {

    private final EstatisticaService service;
    private final EstatisticaConfiguracaoService configuracaoService;

    @GetMapping("/estatistica")
    public EstatisticaResponse buscar() {
        return service.calcular();
    }

    @GetMapping("/estatistica/intervalo")
    public EstatisticaIntervaloResponse buscarIntervalo() {
        return new EstatisticaIntervaloResponse(
                configuracaoService.buscarIntervaloSegundos()
        );
    }

    @GetMapping("/estatistica/intervalo/historico")
    public List<EstatisticaIntervaloHistoricoResponse> buscarHistoricoIntervalos() {
        return configuracaoService.listarHistorico();
    }

    @PutMapping("/estatistica/intervalo")
    public EstatisticaIntervaloResponse atualizarIntervalo(
            @Valid @RequestBody EstatisticaIntervaloRequest request
    ) {
        long intervaloAtualizado = configuracaoService
                .atualizarIntervaloSegundos(request.intervaloSegundos());

        return new EstatisticaIntervaloResponse(intervaloAtualizado);
    }
}
