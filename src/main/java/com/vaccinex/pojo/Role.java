package com.vaccinex.pojo;

import com.vaccinex.pojo.enums.EnumRoleNameType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    EnumRoleNameType roleName;

    @Column(columnDefinition = "NVARCHAR(255)")
    String displayName;

    @OneToMany(mappedBy = "role")
    List<User> users;

}
