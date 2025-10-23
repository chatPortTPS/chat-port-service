package com.tps.orm.repository;

import java.util.List;

import com.tps.orm.entity.AreaAgente;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AreaAgenteRepository implements PanacheRepository<AreaAgente> {

    public AreaAgente findByAgentIdAndAreaPublicId(Long agentId, String areaPublicId) {
        return find("agent.id = ?1 and publicId = ?2", agentId, areaPublicId).firstResult();
    }

    public List<AreaAgente> findByAgentId(Long agentId) {
        return find("agent.id = ?1", agentId).list();
    }

}
