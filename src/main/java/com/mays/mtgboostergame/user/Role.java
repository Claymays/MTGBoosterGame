package com.mays.mtgboostergame.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@Data
@ToString
@NoArgsConstructor
@Entity
@Table(
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_name", columnNames = "name")
        }
)
public class Role {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
}
