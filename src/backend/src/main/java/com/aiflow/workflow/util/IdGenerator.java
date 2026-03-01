package com.aiflow.workflow.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * ID生成器(简化版雪花算法)
 */
@Component
public class IdGenerator {

    private static final long EPOCH = 1640995200000L; // 2022-01-01 00:00:00
    private static final long SEQUENCE_BITS = 12L;
    private static final long SEQUENCE_MASK = (1L << SEQUENCE_BITS) - 1;

    private final AtomicLong sequence = new AtomicLong(0);
    private long lastTimestamp = -1L;

    /**
     * 生成唯一ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常");
        }

        if (timestamp == lastTimestamp) {
            long seq = sequence.incrementAndGet() & SEQUENCE_MASK;
            if (seq == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence.set(0);
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << SEQUENCE_BITS) | sequence.get();
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
