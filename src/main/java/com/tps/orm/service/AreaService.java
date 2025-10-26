package com.tps.orm.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import services.operacion.area.dto.AreaResponse;

import com.tps.orm.repository.AreaRepository;
import com.tps.orm.entity.Area;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AreaService {

    @Inject
    AreaRepository areaRepository;
 
    @Transactional
    public void save(String name, String publicId) { 

        Area area = areaRepository.getAreaByPublicId(publicId);

        if (area == null) { 
            area = new Area();  
        }

        area.setPublicId(publicId);
        area.setName(name);
        areaRepository.persist(area);
        
    }

    @Transactional
    public List<AreaResponse> getAllAreas() { 
        return areaRepository.listAll()
            .stream()
            .map(area -> {
                AreaResponse response = new AreaResponse();
                response.setPublicId(area.getPublicId());
                response.setName(area.getName());
                response.setId(area.getId());
                return response;
            })
            .collect(Collectors.toList()); 
    }

}
