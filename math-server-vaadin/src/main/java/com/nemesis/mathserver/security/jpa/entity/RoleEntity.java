package com.nemesis.mathserver.security.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "role")
public class RoleEntity implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = -1562723181766676778L;

    @Id
    @SequenceGenerator(name = "role_sequence_gen", sequenceName = "s_role")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "role_sequence_gen")
    Integer id;
    private String role;

    public RoleEntity() {
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users = new ArrayList<>();

    @Override
    public String getAuthority() {
        return this.role;
    }
}