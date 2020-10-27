package com.zwzch.fool.repo.model;

import com.zwzch.fool.common.IBase;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.repo.config.SliceConfig;

import java.util.List;

/**
 * 读写分离聚合
 * */
public class Slice implements IBase {
    private String name;
    private String writerName;
    private boolean iswriteable;
    private List<String> readerNameArray;
    private List<Integer> readerWeightArray;
    //读写分离
    private int[] readerUsedArray;
    private int readerIndex;
    //slave
    private int slaveIndex;

    private SliceConfig config;

    public void init(SliceConfig config) throws CommonExpection {
        if(config==null) {
            throw new CommonExpection("Slice init - config is null");
        }


        this.name = config.getName();
        log.info("Slice init - init start - name:" + name);

        this.writerName = config.getWriter();
        this.iswriteable = config.isWriteable();
        this.readerNameArray = config.getReader();
        this.readerWeightArray = config.getReaderWeight();

        this.readerIndex = 0;

        this.slaveIndex = 0;

        this.readerUsedArray = new int[this.readerWeightArray.size()];

        /* 构建各个reader的使用量 */
        for(int i=0; i<readerWeightArray.size(); i++) {
            this.readerUsedArray[i] = readerWeightArray.get(i);
        }

        this.config = config;

        log.info("Slice init - init end - name:" + name);
    }

    public synchronized String getPDBName(boolean isUpdate) throws CommonExpection{
        if(isUpdate) {
            if(iswriteable) {
                return this.writerName;
            } else {
                return null;
            }
        }
        int index = getSlaveIndex();
        if(index >= 0) {
            return readerNameArray.get(index);
        }
        // 权重减完 重新设置权重
        for(int i=0; i<readerWeightArray.size(); i++) {
            readerUsedArray[i] = readerWeightArray.get(i);
        }
        index = getSlaveIndex();
        if(index < 0) {
            throw new CommonExpection("don't have reader, sliceName:" + name );
        } else {
            return readerNameArray.get(index);
        }
    }

    /**
    * 根据权重轮询取从库
    * */
    private int getSlaveIndex() {
        int size = readerNameArray.size();
        for(int i=0; i<size; i++) {
            int index = (i+readerIndex)%size;
            int weight = readerUsedArray[index];
            if(weight > 0) {
                //权重 - 1
                readerUsedArray[index] = weight-1;
                //mod index+1
                readerIndex = (index+1)%size;
                return index;
            }
        }

        return -1;
    }

    public synchronized String getSlave() throws CommonExpection {
        if (this.readerNameArray.size() != 0 && (this.readerNameArray.size() != 1 || !this.writerName.equals(this.readerNameArray.get(0)))) {
            String pdbName = (String)this.readerNameArray.get(this.slaveIndex);
            if (pdbName.equals(this.writerName)) {
                this.slaveIndex = (this.slaveIndex + 1) % this.readerNameArray.size();
                pdbName = (String)this.readerNameArray.get(this.slaveIndex);
            }
            //增加slaveIndex 轮询Slave
            this.slaveIndex = (this.slaveIndex + 1) % this.readerNameArray.size();
            return pdbName;
        } else {
            return null;
        }
    }

}
