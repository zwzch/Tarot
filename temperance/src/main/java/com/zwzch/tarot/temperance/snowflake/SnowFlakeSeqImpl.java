package com.zwzch.tarot.temperance.snowflake;

import com.zwzch.tarot.temperance.ISeq;

import java.util.Random;

public class SnowFlakeSeqImpl implements ISeq {
    //workerId长度
    private final long workerIdBits = 10L;
    //sequence长度
    private final long sequenceBits = 12L;
    //起始时间戳
    private final long twepoch = 1288834974657L;
    //workerId需要的偏移量
    private final long workerIdShift = sequenceBits;
    //timestamp需要的偏移量
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    //sequence掩码 确定在四位数字
    private final long sequenceMask = ~(-1L << sequenceBits);

    private long workerId;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

    private static final Random RANDOM = new Random();


    public SnowFlakeSeqImpl() {
        this.workerId = 1234;

    }

    @Override
    public long genrateId(String tag) throws Exception {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            //当前时间戳比上一个时间戳小 报错
            throw new IllegalAccessException("NTP SERVER IN ERROR");
        } else if (lastTimestamp == timestamp) {
            //和当前时间戳相同
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0){
                //seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //新的ms
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        long id = ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        return id;
    }

    @Override
    public boolean init() {
        return true;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
