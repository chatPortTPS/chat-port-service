package com.tps.orm.repository;

import com.tps.orm.entity.AreaUsuario;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AreasUsuarioRepository implements PanacheRepository<AreaUsuario> {
 
}
