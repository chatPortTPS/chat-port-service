package com.tps.orm.repository;

import java.util.List;

import com.tps.orm.entity.Agent;
import com.tps.orm.entity.TokenAgente;
import com.tps.orm.entity.Tokens;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenAgenteRepository implements PanacheRepository<TokenAgente> {

    public TokenAgente getTokenAgenteByTokenAndAgent(Tokens tokenEntity, Agent agentEntity) {
        return find("token = ?1 and agent = ?2", tokenEntity, agentEntity).firstResult();
    }

    public List<TokenAgente> getTokenAgentesByToken(Tokens tokenEntity) {
        return list("token = ?1", tokenEntity);
    }

}
