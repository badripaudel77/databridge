package com.example.datasyncapp.enums;

import lombok.extern.slf4j.Slf4j;

/**
 * Enum is just like a class that can have methods, constructors, and fields.
 * DOCS REF: https://dev.java/learn/classes-objects/enums/
 */
@Slf4j
public enum ProviderFieldEnum {

    PROVIDER_NAME("providerName"),
    PROVIDER_ID("providerId"),
    PROVIDER_SPECIALTY("providerSpecialty"),
    PROVIDER_TIN("providerTIN");

    private final String field;

    ProviderFieldEnum(String field) {
        this.field = field;
    }

    // returns what is inside each field, like providerName if it is PROVIDER_NAME
    public String getField() {
        return field;
    }

    public ProviderFieldEnum[] getAllEnumValues() {
        return ProviderFieldEnum.values();
    }

    public ProviderFieldEnum getProviderFieldEnumByValue(String value) {
        return ProviderFieldEnum.valueOf(value);
    }

    public String getValueByEnum(ProviderFieldEnum providerFieldEnum) {
        return providerFieldEnum.getField();
    }

    @Override
    public String toString() {
        log.info("Name : {}, same as : {} ", ProviderFieldEnum.PROVIDER_NAME.name(), ProviderFieldEnum.valueOf("providerName"));
        return super.toString();
    }
}
