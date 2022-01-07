package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "club")
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Club extends BaseEntity {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    public Club(String name) {
        this.name = name;
    }

    public Club(Long id, String name) {
        this.setId(id);
        this.name = name;
    }
}
