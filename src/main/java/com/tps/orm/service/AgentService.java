package com.tps.orm.service;

import java.util.List;

import com.tps.orm.entity.Agent;
import com.tps.orm.entity.AgentPosition;
import com.tps.orm.entity.AgentStatus;
import com.tps.orm.entity.AgentTheme;
import com.tps.orm.entity.AgentType;
import com.tps.orm.repository.AgentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import services.operacion.agentes.dto.AgentRequest;
import services.operacion.agentes.dto.AgentResponse;

@ApplicationScoped
public class AgentService {

    @Inject
    AgentRepository agentRepository;

    @Transactional
    public AgentResponse createAgent(AgentRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del agente no puede estar vacío");
        }

        if (request.getUserCreate() == null || request.getUserCreate().trim().isEmpty()) {
            throw new IllegalArgumentException("El creador del agente no puede estar vacío");
        }
  
        Agent agent = new Agent();
        agent.setName(request.getName().trim());
        agent.setDescription(request.getDescription());
        agent.setPrompt(request.getPrompt());
        agent.setStatus(request.getStatus() != null ? request.getStatus() : AgentStatus.DESARROLLO);
        agent.setTheme(request.getTheme() != null ? request.getTheme() : AgentTheme.getDefault());
        agent.setPosition(request.getPosition() != null ? request.getPosition() : AgentPosition.getDefault());
        agent.setWebsite(request.getWebsite());
        agent.setType(request.getType() != null ? request.getType() : AgentType.getDefault());
        agent.setUserCreate(request.getUserCreate().trim());
         
        agentRepository.persist(agent);

        return AgentResponse.fromEntity(agent);
    }


    @Transactional
    public List<AgentResponse> getAllAgents() {
        List<Agent> agents = agentRepository.listAll();
        return agents.stream().map(AgentResponse::fromEntity).toList();
    }
    

}
