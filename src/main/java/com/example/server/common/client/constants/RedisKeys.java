package com.example.server.common.client.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKeys {
    DEVICE_TOKEN_KEY("deviceTokens");

    private final String key;

}
