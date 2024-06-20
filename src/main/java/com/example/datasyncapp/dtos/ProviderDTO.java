package com.example.datasyncapp.dtos;

import java.io.Serializable;

/**
 * @param providerId
 * @param providerName
 * @param providerSpecialty
 * @param providerTIN
 * Record works as DTO , it also implements Serializable because Java Object will be serialized or De-serialized
 * while storing in redis hash.
 */
public record ProviderDTO(Long providerId, String providerName, String providerSpecialty, Long providerTIN) implements Serializable{

}
