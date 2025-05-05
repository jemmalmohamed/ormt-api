package ma.org.ormt.modules.users.roleacces.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role_acces")
public class RoleAcces extends BaseEntity {

    @Column(name = "role_code", nullable = false)
    private String roleCode;

    @Column(name = "type_ressource", nullable = false)
    private String typeRessource;

    @Column(name = "ressource_id", nullable = false)
    private Long ressourceId;

    @Column(name = "niveau_acces", nullable = false)
    private String niveauAcces;

    private String description;

}
