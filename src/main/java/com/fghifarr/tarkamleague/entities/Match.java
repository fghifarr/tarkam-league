package com.fghifarr.tarkamleague.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "`match`")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Date cannot be null!")
    private LocalDateTime kickOff;
    // Still manual, match based on date time
    // will be implemented in the future updates
    private boolean isFinished = false;

    // Will be added in the future
//    @ManyToOne
//    @JoinColumn(name = "season_id")
//    private Season season;

    @OneToOne
    @JoinColumn(name = "host_id")
    private MatchHost host;

    @OneToOne
    @JoinColumn(name = "visitor_id")
    private MatchVisitor visitor;
}
