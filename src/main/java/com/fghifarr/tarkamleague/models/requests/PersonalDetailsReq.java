package com.fghifarr.tarkamleague.models.requests;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalDetailsReq {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of Birth cannot be null!")
    private Date dob;
    @NotBlank(message = "Nationality cannot be blank!")
    private String nationality;
    private Integer height;

    public java.sql.Date getSqlDob() {
        return new java.sql.Date(this.dob.getTime());
    }
}
