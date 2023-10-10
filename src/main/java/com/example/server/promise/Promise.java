package com.example.server.promise;

import com.example.server.member.Authority;
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
public class Promise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String location;

    private String coordinate;

    private String penalty;

    private String organizer;

    private String date;

    private String memo;

//    private int cnt;

    @OneToMany(mappedBy = "promise", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<PromiseMember> members = new ArrayList<>();

    public void setMembers(List<PromiseMember> members) {
        this.members = members;
//        this.cnt = members.size();
        members.forEach(o -> o.setPromise(this));
    }
}
