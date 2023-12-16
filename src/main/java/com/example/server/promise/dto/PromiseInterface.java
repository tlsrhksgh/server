package com.example.server.promise.dto;

import com.example.server.friend.dto.FriendInterface;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface PromiseInterface {

    String getId();
    String getDate();
    String getLocation();
    String getMemo();
    String getLeader();
    String getPenalty();
    String getTitle();
}
