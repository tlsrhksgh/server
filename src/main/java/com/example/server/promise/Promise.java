package com.example.server.promise;

import com.example.server.common.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Promise extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String location;

    private String nickname;

    private String coordinate;

    private String penalty;

    private String leader;

    private String date;

    private String memo;

    private String completed;

    @JsonManagedReference
    @OneToMany(mappedBy = "promise", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PromiseMember> members = new ArrayList<>();

    public void setMembers(List<PromiseMember> members) {
        this.members = members;
//        this.cnt = members.size();
        members.forEach(o -> o.setPromise(this));
    }
}
