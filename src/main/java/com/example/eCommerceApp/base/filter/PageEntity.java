package com.example.eCommerceApp.base.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class PageEntity <T>{
    private List<T> content;
    private Integer pageSize;
    private Integer numberOfElements;
}
