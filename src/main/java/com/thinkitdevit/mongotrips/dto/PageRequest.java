package com.thinkitdevit.mongotrips.dto;

import lombok.Data;

@Data
public class PageRequest<T> {

    private int page;
    private int size;
    private T filter;

}
