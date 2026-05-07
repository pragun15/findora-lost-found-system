package com.lostandfound.findora.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Avoid circular JSON (User -> Items -> Reporter -> User ...)
    @JsonIgnore
    @OneToMany(mappedBy = "reporter", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Item> reportedItems = new ArrayList<>();

    // Avoid circular JSON (User -> Claims -> Claimant -> User ...)
    @JsonIgnore
    @OneToMany(mappedBy = "claimant", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Claim> claims = new ArrayList<>();
}
