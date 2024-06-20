package com.example.datasyncapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Entity
@Table(name = "providers")
@AllArgsConstructor
@NoArgsConstructor
@Data
// @Document // Can also be made document and entity at a time, can have two same repositories for mongo and RDBMS
public class Provider implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "provider_name")
    private String name;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "provider_tin")
    private Long providerTIN;
}
