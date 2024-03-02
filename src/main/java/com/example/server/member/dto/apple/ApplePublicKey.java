package com.example.server.member.dto.apple;

import lombok.Getter;

@Getter
public class ApplePublicKey {
    private String kty;
    private String kid;
    private String alg;
    private String n;
    private String e;
}
