package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Entity
@Table(name = "personal_details")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDetails {

    private static final long serialVersionUID = 1L;

    @Id @Column(name = "player_id")
    private Long id;

    @OneToOne(mappedBy = "profile")
    @MapsId @JoinColumn(name = "player_id")
    private Player player;

    @NotNull(message = "Date of Birth cannot be null!")
    private Date dob;
    @NotBlank(message = "Nationality cannot be blank!")
    private String nationality;
    private Integer height;
}
