package com.tps.orm.service;

import java.time.LocalDateTime;
import java.util.List;

import com.tps.orm.entity.Agent;
import com.tps.orm.entity.AgentPosition;
import com.tps.orm.entity.AgentStatus;
import com.tps.orm.entity.AgentTheme;
import com.tps.orm.entity.AgentType;
import com.tps.orm.entity.AreaAgente;
import com.tps.orm.repository.AgentRepository;
import com.tps.orm.repository.AreaAgenteRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import services.operacion.agentes.dto.AgentRequest;
import services.operacion.agentes.dto.AgentResponse;

@ApplicationScoped
public class AgentService {

    @Inject
    AgentRepository agentRepository;

    @Inject
    AreaAgenteRepository areaAgenteRepository;

    @Transactional
    public AgentResponse createAgent(AgentRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del agente no puede estar vacío");
        }

        if (request.getUserCreate() == null || request.getUserCreate().trim().isEmpty()) {
            throw new IllegalArgumentException("El creador del agente no puede estar vacío");
        }

        if (request.getName().trim().equals("Central ChatPort")) {
            throw new IllegalArgumentException("El nombre del agente no puede ser 'Central ChatPort'");
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
        
        // Inicializar timestamps manualmente (para que funcione en tests mockito)
        LocalDateTime now = LocalDateTime.now();
        agent.setCreatedAt(now);
        agent.setUpdatedAt(now);
        agent.setIntranet(true);
         
        agentRepository.persist(agent);

        return AgentResponse.fromEntity(agent);
    }


    @Transactional
    public AgentResponse getCentralAgent() {
        List<Agent> agents = agentRepository.findByName("Central ChatPort");
  
        if (agents.isEmpty()) {
            
            //crear el agente central si no existe
            Agent centralAgent = new Agent();
            centralAgent.setName("Central ChatPort");
            centralAgent.setDescription("Agente central para gestión de consultas generales");
            centralAgent.setPrompt("Eres el agente central de ChatPort, encargado de gestionar consultas generales y redirigir a los agentes especializados cuando sea necesario.");
            centralAgent.setStatus(AgentStatus.PUBLICADO);
            centralAgent.setTheme(AgentTheme.MINI);
            centralAgent.setPosition(AgentPosition.BOTTOM_RIGHT);
            centralAgent.setType(AgentType.DYNAMIC);
            centralAgent.setWebsite("Current website");
            centralAgent.setUserCreate("system");
            
            // Inicializar timestamps manualmente (para que funcione en tests mockito)
            LocalDateTime now = LocalDateTime.now();
            centralAgent.setCreatedAt(now);
            centralAgent.setUpdatedAt(now);
            centralAgent.setIntranet(true);
            agentRepository.persist(centralAgent);
            return AgentResponse.fromEntity(centralAgent);
            
        }

        return AgentResponse.fromEntity(agents.get(0));
    }

 
    @Transactional
    public List<AgentResponse> getAllAgents() {
        List<Agent> agents = agentRepository.listAll();
        return agents.stream().map(AgentResponse::fromEntity).toList();
    }
    
    @Transactional
    public void vincularAreaAlAgente(Long agentId, String areaPublicId) {
        
        Agent agent = agentRepository.findById(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Agente no encontrado con identificador proporcionado: " + agentId);
        }

        if (areaPublicId == null || areaPublicId.trim().isEmpty()) {
            throw new IllegalArgumentException("El área es obligatoria");
        }

        AreaAgente existingAreaAgente = areaAgenteRepository.findByAgentIdAndAreaPublicId(agentId, areaPublicId);

        if (existingAreaAgente != null) {
            throw new IllegalArgumentException("El área ya está vinculada al agente");
        }

        AreaAgente areaAgente = new AreaAgente();
        areaAgente.setPublicId(areaPublicId);
        areaAgente.setAgent(agent);

        areaAgenteRepository.persist(areaAgente);

    }

    @Transactional
    public boolean deleteAgentById(Long agentId) {
        Agent agent = agentRepository.findById(agentId);

        if (agent == null) {
            throw new IllegalArgumentException("Agente no encontrado con ID: " + agentId);
        }

        List<AreaAgente> areaAgentes = areaAgenteRepository.findByAgentId(agentId);

        for (AreaAgente areaAgente : areaAgentes) {
            areaAgenteRepository.delete(areaAgente);
        }

        agentRepository.delete(agent);
        return true;
    }


    @Transactional
    public List<AgentResponse> getAgentByPublicId(String publicId) {
         
        List<Agent> agents = agentRepository.findByPublicId(publicId);

        if (agents.isEmpty()) {
            throw new IllegalArgumentException("Agente no encontrado con uuid: " + publicId);
        }   

        Agent agent = agents.get(0);

        List<String> vinculos = new java.util.ArrayList<>();

        if (agent.getType().equals(AgentType.AREAS)) {
            List<AreaAgente> areas = areaAgenteRepository.findByAgentId(agent.getId());
            vinculos = areas.stream().map(AreaAgente::getPublicId).toList(); 
        }

        return List.of(AgentResponse.fromEntity(agent, vinculos));

    }


    @Transactional
    public AgentResponse updateAgent(Long agentId, String name, String description, String prompt, String theme, String position,
            String website) {
        
        Agent agent = agentRepository.findById(agentId);

        if (agent == null) {
            throw new IllegalArgumentException("Agente no encontrado con ID: " + agentId);
        }

        if (name != null && !name.trim().isEmpty()) {
            agent.setName(name.trim());
        }

        if (description != null && !description.trim().isEmpty()) {
            agent.setDescription(description);
        }

        if (prompt != null && !prompt.trim().isEmpty()) {
            agent.setPrompt(prompt);
        }

        if (theme != null && !theme.trim().isEmpty()) {
            try {
                AgentTheme agentTheme = AgentTheme.valueOf(theme.trim().toUpperCase());
                agent.setTheme(agentTheme);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valor de tema inválido: " + theme + ". Valores válidos son: " + java.util.Arrays.toString(AgentTheme.values()));
            }
        }


        if (position != null && !position.trim().isEmpty()) {
            try {
                AgentPosition agentPosition = AgentPosition.valueOf(position.trim().toUpperCase());
                agent.setPosition(agentPosition);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valor de posición inválido: " + position + ". Valores válidos son: " + java.util.Arrays.toString(AgentPosition.values()));
            }
        }

        if (website != null && !website.trim().isEmpty()) {
            agent.setWebsite(website);
        }

        agentRepository.persist(agent);

        return  AgentResponse.fromEntity(agent);
                
    }


    @Transactional
    public AgentResponse publishAgent(Long agentId) {
        
        Agent agent = agentRepository.findById(agentId);

        if (agent == null) {
            throw new IllegalArgumentException("Agente no encontrado con ID: " + agentId);
        }

        agent.setStatus(AgentStatus.PUBLICADO);

        agentRepository.persist(agent);

        return AgentResponse.fromEntity(agent);
        
    }

    @Transactional
    public AgentResponse deactivateAgent(Long agentId) {
        
        Agent agent = agentRepository.findById(agentId);

        if (agent == null) {
            throw new IllegalArgumentException("Agente no encontrado con ID: " + agentId);
        }

        agent.setStatus(AgentStatus.DESACTIVADO);

        agentRepository.persist(agent);

        return AgentResponse.fromEntity(agent);
        
        
    }
    
    @Transactional
    public boolean changeIntranetStatus(Long agentId, Boolean intranet) {
        Agent agent = agentRepository.findById(agentId);

        if (agent == null) {
            throw new IllegalArgumentException("Agente no encontrado con ID: " + agentId);
        }

        agent.setIntranet(intranet);
        agentRepository.persist(agent);
        return true;
    }

}
