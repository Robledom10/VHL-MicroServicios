package com.documents.dto;

import com.documents.entity.TravelerDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelerFileDTO {

    private Integer userId;

    private Integer reservationId;

    private List<TravelerDocument> documents;

}