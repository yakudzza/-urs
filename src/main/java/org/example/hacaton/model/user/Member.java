package org.example.hacaton.model.user;


import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "type_dev")
    @Enumerated(EnumType.STRING)
    private TypeDev typeDev;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}
