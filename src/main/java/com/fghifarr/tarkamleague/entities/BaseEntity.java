package com.fghifarr.tarkamleague.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue
    private Long id;

    private Date lastUpdated = new Date();
    private Date dateCreated = new Date();
}
