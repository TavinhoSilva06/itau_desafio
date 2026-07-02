package com.example.ITAUtask.repository;

import com.example.ITAUtask.model.Transacao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransacaoRepository extends MongoRepository
        <
        Transacao,
        String
        > {
}