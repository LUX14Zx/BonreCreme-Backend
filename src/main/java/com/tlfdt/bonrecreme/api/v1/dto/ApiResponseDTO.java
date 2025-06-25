package com.tlfdt.bonrecreme.api.v1.dto;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseDTO<Data> implements Serializable {
    private Data api_data;
    private String status;
    private String message;

    public String getTimestamp() {
        return LocalDateTime.now().toString();
    }
}