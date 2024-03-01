package com.shelter.bot.entity;

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
@Table(schema = "public", name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "username", nullable = false)
    private String username;
}