package com.example.pool.spring.boot.start.result;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = -2474596551402989285L;

    private String code;
    private String info;
    private T data;



}

