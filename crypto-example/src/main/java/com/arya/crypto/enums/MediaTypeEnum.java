package com.arya.crypto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum MediaTypeEnum {
    APPLICATION_JSON("application/json; charset=utf-8"),
    FORM_URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART("multipart/form-data");

    private final String type;
}
