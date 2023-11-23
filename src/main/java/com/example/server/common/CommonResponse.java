package com.example.server.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse {

    private String resultCode;

    private String resultMessage;

    private Map<String, Object> data;

}