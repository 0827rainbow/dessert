package com.example.shixun7.entity;

import lombok.Data;
import java.util.List;

@Data
public class TableDataInfo {
    private long total;
    private List<?> rows;
    private int code;
    private String msg;
    private long timestamp;

    public TableDataInfo() {
        this.code = 200;
        this.msg = "success";
        this.timestamp = System.currentTimeMillis();
    }
}