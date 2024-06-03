package com.thinkitdevit.mongotrips.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse <T>{

    private int page;
    private int size;
    private long total;
    private List<T> data;

}
