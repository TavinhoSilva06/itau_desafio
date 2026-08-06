package com.example.ITAUtask.service;

import com.example.ITAUtask.exception.TransacaoInvalidaException;
import com.example.ITAUtask.model.Transacao;
import com.example.ITAUtask.dto.TransacaoRequest;
import com.example.ITAUtask.dto.TransacaoResponse;
import com.example.ITAUtask.repository.TransacaoRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j // Anotação do Lombok que cria o comando 'log' automaticamente
@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository repository;
    private final EstatisticaConfiguracaoService configuracaoService;

    public void registrar(TransacaoRequest request) {


        OffsetDateTime data = OffsetDateTime.now();

        log.info("Data recebida: {}", request.dataHora());
        log.info("Agora servidor: {}", data);

        if (request.dataHora().isAfter(data)) {

            log.warn(
                    "Tentativa de cadastrar transação futura: {}",
                    request.dataHora()
            );

            throw new TransacaoInvalidaException(
                    "Data e hora não podem estar no futuro"
            );
        }

        OffsetDateTime limite = data.minusSeconds(
                configuracaoService.buscarIntervaloSegundos()
        );

        if (request.dataHora().isBefore(limite)) {

            log.warn(
                    "Tentativa de cadastrar transacao fora do limite: {}",
                    request.dataHora()
            );

            throw new TransacaoInvalidaException(
                    "Esta transação já passou do limite "
            );
        }

        Transacao transacao = new Transacao(
                request.valor(),
                request.dataHora().toInstant()
        );


        repository.save(transacao);

        log.info(
                "Transação salva com sucesso"
        );
    }

    public void limpar() {

        log.info(
                "Todas as transações foram removidas"
        );

        repository.deleteAll();
    }

    public List<TransacaoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(transacao -> new TransacaoResponse(
                        transacao.getId(),
                        transacao.getValor(),
                        transacao.getDataHora()
                ))
                .toList();
    }
}
