package com.example.server.promise.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromiseRequestDto {

    private String id;
    private String title;
    private String location;
    private String coordinate;
    private String penalty;
    private String date;
    private String memo;

}
