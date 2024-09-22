package com.mlorenzo.spring6restmvc.entities;

import com.mlorenzo.spring6restmvc.models.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "beers")
public class Beer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    // Para que el UUID se guarde en la base de datos como una cadena de caracteres y no como un dato binario
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Integer version;

    @NotBlank
    @Size(max = 50)
    @Column(length = 50, nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private BeerStyle style;

    @NotBlank
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String upc;

    private Integer quantityOnHand;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "beer")
    private Set<BeerOrderLine> beerOrderLines;

    @ManyToMany(mappedBy = "beers")
    private Set<Category> categories;

    @CreationTimestamp // Para la generación automática de las fechas y tiempos de creación
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp // Para la generación automática de las fechas y tiempos de actualizaión
    @Column(nullable = false)
    private LocalDateTime updateDate;
}
