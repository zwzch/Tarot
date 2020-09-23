package com.zwzch.fool.repo.model;

import java.util.List;

/**
 * 读写分离聚合
 * */
public class Slice {
    private String name;
    private String writeName;
    private boolean iswriteable;
    private List<String> readerNameArray;
    private List<Integer> readerWeightArray;
    private int[] readerUsedArray;
    private int readerIndex;
    private int slaveIndex;

}
