package com.example.ITAUtask.repository;

import com.example.ITAUtask.model.EstatisticaIntervalo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EstatisticaIntervaloRepository
        extends MongoRepository<EstatisticaIntervalo, String> {

    Optional<EstatisticaIntervalo> findFirstByAtivoTrue();

    List<EstatisticaIntervalo> findAllByOrderByDataHoraAlteracaoDesc();
}
