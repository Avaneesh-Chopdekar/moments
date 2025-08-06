package com.moments.backend.payload;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomMessage<T> {
    private String message;
    private boolean success = false;
    private T data;
}
