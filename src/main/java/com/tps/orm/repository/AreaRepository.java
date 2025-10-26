package com.tps.orm.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import com.tps.orm.entity.Area;

@ApplicationScoped
public class AreaRepository implements PanacheRepository<Area> {

    public Area getAreaByPublicId(String publicId) { 
        return find("publicId", publicId).firstResult();   
    }

}
