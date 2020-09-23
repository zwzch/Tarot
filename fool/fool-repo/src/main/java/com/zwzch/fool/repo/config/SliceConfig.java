package com.zwzch.fool.repo.config;

import com.zwzch.fool.common.constant.CommonConfig;

import java.util.List;

public class SliceConfig {
    private String name;
    private String writer;
    boolean isWriteable;
    private List<String> readerArray;
    private List<Integer> readerWeightArray;
    private String binlog;

    private CommonConfig commonConfig;
    public SliceConfig(String name, String writer, boolean isWriteable,
                       List<String> reader, List<Integer> readerWeight,
                       CommonConfig configValue)
    {
        this.name = name;
        this.writer = writer;
        this.isWriteable = isWriteable;
        this.readerArray = reader;
        this.readerWeightArray = readerWeight;
        this.commonConfig = configValue;
    }
}
