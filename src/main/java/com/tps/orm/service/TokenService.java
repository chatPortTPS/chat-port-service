package com.tps.orm.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import com.tps.orm.entity.Agent;
import com.tps.orm.entity.AgentPosition;
import com.tps.orm.entity.AgentStatus;
import com.tps.orm.entity.AgentTheme;
import com.tps.orm.entity.AgentType;
import com.tps.orm.entity.AreaAgente;
import com.tps.orm.repository.AgentRepository;
import com.tps.orm.repository.AreaAgenteRepository;
import com.tps.orm.repository.TokenAgenteRepository;
import com.tps.orm.repository.TokenRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import services.operacion.agentes.dto.AgentRequest;
import services.operacion.agentes.dto.AgentResponse;
import services.operacion.token.dto.TokenResponse;

import com.tps.orm.entity.Tokens;
import com.tps.orm.entity.TokenAgente;
import co.elastic.clients.elasticsearch.core.termvectors.Token;

@ApplicationScoped
public class TokenService {

    @Inject
    AgentRepository agentRepository;

    @Inject 
    TokenRepository tokenRepository;

    @Inject
    TokenAgenteRepository tokenAgenteRepository;

    @Transactional
    public TokenResponse create(String name, Integer diasExpirar, Integer minToExpire, Boolean refreshable, String createdBy) {
        
        Tokens tokenEntity = new Tokens();
        tokenEntity.setName(name);  
        tokenEntity.setStatus(true); 
        tokenEntity.setExpiresDays(diasExpirar);
        tokenEntity.setMinutesActive(minToExpire);
        tokenEntity.setCreatedBy(createdBy);
        tokenEntity.setRefreshable(refreshable);

        tokenRepository.persist(tokenEntity);

        return TokenResponse.fromEntity(tokenEntity);

    }

    @Transactional
    public TokenResponse update(Long id, String name, Integer diasExpirar, Integer minToExpire, Boolean refreshable) throws Exception {
        
        Tokens tokenEntity = tokenRepository.findById(id);

        if (tokenEntity == null) {
            throw new Exception("No se puede encontrar el token con el identificador: " + id);
        }

        if (name != null && !name.isEmpty()) {
            tokenEntity.setName(name);
        }

        if (diasExpirar != null) {
            tokenEntity.setExpiresDays(diasExpirar); 
        }
        
        if (minToExpire != null) {
            tokenEntity.setMinutesActive(minToExpire);
        }

        if (refreshable != null) {
            tokenEntity.setRefreshable(refreshable);
        }

        tokenRepository.persist(tokenEntity);

        return TokenResponse.fromEntity(tokenEntity);

    }

    @Transactional
    public TokenResponse changeStatus(Long id, Boolean status) throws Exception {
        
        Tokens tokenEntity = tokenRepository.findById(id);

        if (tokenEntity == null) {
            throw new Exception("No se puede encontrar el token con el identificador: " + id);
        }

        tokenEntity.setStatus(status);

        tokenRepository.persist(tokenEntity);

        return TokenResponse.fromEntity(tokenEntity);

    }


    @Transactional
    public Boolean vincularAgente(Long tokenId, Long agenteId) throws Exception {
        
        Tokens tokenEntity = tokenRepository.findById(tokenId);

        if (tokenEntity == null) {
            throw new Exception("No se puede encontrar el token con el identificador: " + tokenId);
        }

        Agent agentEntity = agentRepository.findById(agenteId);

        if (agentEntity == null) {
            throw new Exception("No se puede encontrar el agente con el identificador: " + agenteId);
        }

        TokenAgente tokenAgente = new TokenAgente();

        tokenAgente.setToken(tokenEntity);
        tokenAgente.setAgent(agentEntity);
        tokenAgenteRepository.persist(tokenAgente);

        return true;

    }

    @Transactional
    public Boolean desvincularAgente(Long tokenId, Long agenteId) throws Exception {
        
        Tokens tokenEntity = tokenRepository.findById(tokenId);

        if (tokenEntity == null) {
            throw new Exception("No se puede encontrar el token con el identificador: " + tokenId);
        }

        Agent agentEntity = agentRepository.findById(agenteId);

        if (agentEntity == null) {
            throw new Exception("No se puede encontrar el agente con el identificador: " + agenteId);
        }

        TokenAgente tokenAgente = tokenAgenteRepository.getTokenAgenteByTokenAndAgent(tokenEntity, agentEntity);

        if (tokenAgente == null) {
            throw new Exception("No se puede encontrar la vinculacion entre el token y el agente");
        }

        tokenAgenteRepository.delete(tokenAgente);

        return true;

    } 

    @Transactional
    public Boolean deleteToken(Long id) throws Exception {
        Tokens tokenEntity = tokenRepository.findById(id);

        if (tokenEntity == null) {
            throw new Exception("No se puede encontrar el token con el identificador: " + id);
        }

        List<TokenAgente> tokenAgentes = tokenAgenteRepository.getTokenAgentesByToken(tokenEntity);

        for (TokenAgente ta : tokenAgentes) {
            tokenAgenteRepository.delete(ta);
        }

        tokenRepository.delete(tokenEntity);
        return true;
    }

    @Transactional
    public List<TokenResponse> getAllTokens() {
        List<Tokens> tokens = tokenRepository.listAll(); 
        return tokens.stream().map(TokenResponse::fromEntity).toList();
    }

    @Transactional
    public TokenResponse getTokenById(Long id) throws Exception {
        Tokens tokenEntity = tokenRepository.findById(id);

        if (tokenEntity == null) {
            throw new Exception("No se puede encontrar el token con el identificador: " + id);
        }

        List<TokenAgente> tokenAgentes = tokenAgenteRepository.getTokenAgentesByToken(tokenEntity);

        return TokenResponse.fromEntity(tokenEntity, tokenAgentes);
    }

}
