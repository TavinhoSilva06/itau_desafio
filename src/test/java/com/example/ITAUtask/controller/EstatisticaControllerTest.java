package com.example.ITAUtask.controller;

import com.example.ITAUtask.dto.EstatisticaIntervaloHistoricoResponse;
import com.example.ITAUtask.dto.EstatisticaResponse;
import com.example.ITAUtask.service.EstatisticaConfiguracaoService;
import com.example.ITAUtask.service.EstatisticaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstatisticaController.class)
class EstatisticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstatisticaService service;

    @MockitoBean
    private EstatisticaConfiguracaoService configuracaoService;

    @Test
    void deveRetornarEstatisticas() throws Exception {

        when(service.calcular())
                .thenReturn(
                        new EstatisticaResponse(
                                1,
                                BigDecimal.valueOf(100),
                                BigDecimal.valueOf(100),
                                BigDecimal.valueOf(100),
                                BigDecimal.valueOf(100)
                        )
                );

        mockMvc.perform(
                        get("/estatistica")
                )
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornarIntervaloAtual() throws Exception {

        when(configuracaoService.buscarIntervaloSegundos())
                .thenReturn(3600L);

        mockMvc.perform(
                        get("/estatistica/intervalo")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intervaloSegundos").value(3600));
    }

    @Test
    void deveAtualizarIntervalo() throws Exception {

        when(configuracaoService.atualizarIntervaloSegundos(120L))
                .thenReturn(120L);

        String json = """
                {
                    "intervaloSegundos": 120
                }
                """;

        mockMvc.perform(
                        put("/estatistica/intervalo")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intervaloSegundos").value(120));
    }

    @Test
    void deveRetornarHistoricoDeIntervalos() throws Exception {

        when(configuracaoService.listarHistorico())
                .thenReturn(List.of(
                        new EstatisticaIntervaloHistoricoResponse(
                                120L,
                                Instant.parse("2026-06-25T03:10:00Z"),
                                true
                        ),
                        new EstatisticaIntervaloHistoricoResponse(
                                60L,
                                Instant.parse("2026-06-25T03:00:00Z"),
                                false
                        )
                ));

        mockMvc.perform(
                        get("/estatistica/intervalo/historico")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].intervaloSegundos").value(120))
                .andExpect(jsonPath("$[0].ativo").value(true))
                .andExpect(jsonPath("$[1].intervaloSegundos").value(60))
                .andExpect(jsonPath("$[1].ativo").value(false));
    }

    @Test
    void deveRetornar400QuandoIntervaloForInvalido() throws Exception {

        String json = """
                {
                    "intervaloSegundos": 0
                }
                """;

        mockMvc.perform(
                        put("/estatistica/intervalo")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }
}
