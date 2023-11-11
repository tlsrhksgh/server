package com.example.server.promise;

import com.example.server.member.Authority;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener
        .class)
public class Promise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String location;

    private String coordinate;

    private String penalty;

    private String leader;

    private String date;

    private String memo;

    private String completed;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @JsonManagedReference
    @OneToMany(mappedBy = "promise", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<PromiseMember> members = new ArrayList<>();

    public void setMembers(List<PromiseMember> members) {
        this.members = members;
//        this.cnt = members.size();
        members.forEach(o -> o.setPromise(this));
    }
}
