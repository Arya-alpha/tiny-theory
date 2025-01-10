package com.arya.crypto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@ToString
@AllArgsConstructor
public enum MediaTypeEnum {
    APPLICATION_JSON("application/json; charset=utf-8"),
    FORM_URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART("multipart/form-data");

    private final String type;

    public static final Map<String, MediaTypeEnum> MEDIA_TYPE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(
                    MediaTypeEnum::getType,
                    Function.identity(),
                    (a, b) -> a
            ));

    public static MediaTypeEnum getMediaType(String type) throws IllegalAccessException {
        if (!MEDIA_TYPE_MAP.containsKey(type)) {
            throw new IllegalAccessException("");
        }

        return MEDIA_TYPE_MAP.get(type);
    }
}
