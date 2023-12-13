package com.cardpulse.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tbl_card_info")
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "card_id")
    Long id;
    @Column(
            name = "bank_identification_number"
    )
    Integer bin;
    @Column(
            name = "scheme"
    )
    String scheme;
    @Column(
            name = "type"
    )
    String type;
    @Column(
            name = "bank"
    )
    String bank;
    @Column(
            name = "number_of_hits"
    )
    Integer numberOfHits;
}
