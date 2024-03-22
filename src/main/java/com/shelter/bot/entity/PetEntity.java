package com.shelter.bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "pets")
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Генерация ID
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonIgnore
    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity owner;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "breed", nullable = false)
    private String breed;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter;
}
