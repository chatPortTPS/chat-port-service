package com.tps.orm.repository;

import com.tps.orm.entity.Agent;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgentRepository  implements PanacheRepository<Agent> {

}
