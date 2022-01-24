package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "club")
    private Set<SeasonClub> seasons = new HashSet<>();

    //ToDO: moved into season club entity
    @OneToMany(mappedBy = "club")
    private Set<Player> players = new HashSet<>();

    public Club(String name) {
        this.name = name;
    }

    public Club(Long id, String name) {
        this(name);
        this.setId(id);
    }
}
