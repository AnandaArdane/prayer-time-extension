package com.bpd.prayertime.content.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Content {

    @Id
    @SequenceGenerator(
            name = "content_seq",
            sequenceName = "content_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "content_seq"
    )
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Boolean active;
}
