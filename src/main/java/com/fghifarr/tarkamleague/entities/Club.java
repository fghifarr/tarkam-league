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
@AllArgsConstructor
public class Club extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    public Club(Long id, String name) {
        this(name);
        this.setId(id);
    }
}
