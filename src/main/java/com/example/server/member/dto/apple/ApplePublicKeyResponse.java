package com.example.server.member.dto.apple;

import lombok.Getter;

import javax.naming.AuthenticationException;
import java.util.List;

@Getter
public class ApplePublicKeyResponse {
    List<ApplePublicKey> keys;

    public ApplePublicKey getMatchedKey(String kid, String alg) throws AuthenticationException {
        return keys.stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findAny()
                .orElseThrow(AuthenticationException::new);
    }
}
