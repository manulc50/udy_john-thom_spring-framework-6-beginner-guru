package com.mlorenzo.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // Para que el UUID se guarde en la base de datos como una cadena de caracteres y no como un dato binario
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private String description;

    @CreationTimestamp // Para la generación automática de las fechas y tiempos de creación
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp // Para la generación automática de las fechas y tiempos de actualizaión
    @Column(nullable = false)
    private LocalDateTime updateDate;

    @ManyToMany
    @JoinTable(name = "beers_categories",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id"))
    private Set<Beer> beers;
}
