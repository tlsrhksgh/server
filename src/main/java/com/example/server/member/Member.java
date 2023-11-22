package com.example.server.member;

import com.example.server.chat.domain.model.entity.MemberChatRoom;
import com.example.server.member.dto.MemberUpdateRequest;
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

    @Column(unique = true)
    private String account;

    private String password;

    private String nickname;

    private Integer level;

    private Integer exp;

    @Lob
    private String img;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Authority> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private Set<MemberChatRoom> memberChatRooms = new HashSet<>();

    public void setRoles(List<Authority> roles) {
        this.roles = roles;
        roles.forEach(o -> o.setMember(this));
    }

    public void update(MemberUpdateRequest request) {
        this.nickname = Objects.isNull(request.getNickname()) ? nickname : request.getNickname();
        this.img = Objects.isNull(request.getImg()) ? img : request.getImg();
        this.password = Objects.isNull(request.getPassword()) ? password : request.getPassword();
    }
}
