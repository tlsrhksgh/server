package com.example.server.member;

import com.example.server.chat.domain.model.entity.MemberChatRoom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @Column(unique = true, updatable = false)
    private String account;

    private String password;

    @Column(unique = true)
    private String nickname;

    private Integer level;

    private Integer exp;

    @Lob
    private String img;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Authority> roles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<MemberChatRoom> memberChatRooms = new HashSet<>();

    public void setRoles(List<Authority> roles) {
        this.roles = roles;
        roles.forEach(o -> o.setMember(this));
    }
}
