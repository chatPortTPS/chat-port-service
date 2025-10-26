package com.tps.orm.repository;

import java.util.List;

import com.tps.orm.entity.Agent;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgentRepository  implements PanacheRepository<Agent> {

    public List<Agent> findByPublicId(String publicId) { 
        return find("publicId = ?1", publicId).list();
    }

    public List<Agent> findByName(String name) { 
        return find("name = ?1", name).list();
    }

}
