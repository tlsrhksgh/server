package com.example.server.config;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Configuration
public class ApnsConfig {

    @Value("${apple.key-path}")
    private String path;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key}")
    private String keyName;

    @Value("${apple.key-id}")
    private String keyId;


    @Bean
    ApnsClient apnsClient() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String keyPathAndName = path + keyName;
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(keyPathAndName);

        if(Objects.isNull(is)) {
            throw new FileNotFoundException("File not found: " + keyPathAndName);
        }

        return new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                .setSigningKey(ApnsSigningKey.loadFromInputStream(is, teamId, keyId))
                .build();
    }
}
