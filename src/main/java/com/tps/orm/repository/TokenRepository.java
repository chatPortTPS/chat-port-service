package com.tps.orm.repository;
 
import com.tps.orm.entity.Tokens;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenRepository implements PanacheRepository<Tokens> {

}
