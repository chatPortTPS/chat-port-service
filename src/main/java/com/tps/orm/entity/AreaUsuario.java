package com.tps.orm.entity;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Immutable
@Table(name = "V_AREAS_USUARIO", schema = "SGD")
public class AreaUsuario implements Serializable {

    @Id
    @Column(name = "CORREO")
    private String correo;

    @Column(name = "NOMBRE_ORIGINAL")
    private String nombreOriginal;

    @Column(name = "NOMBRE_NORMALIZADO")
    private String nombreNormalizado;

    @Column(name = "TIPO")
    private String tipo;

    public AreaUsuario() {
    }
 
}
