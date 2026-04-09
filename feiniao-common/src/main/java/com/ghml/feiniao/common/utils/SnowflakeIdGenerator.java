package com.ghml.feiniao.common.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * 雪花算法ID生成器。
 * <p>
 * 64位结构（从高到低）：
 * <pre>
 * 0 - 41位时间戳 - 5位数据中心ID - 5位机器ID - 12位序列号
 * </pre>
 * <ul>
 *   <li>1位符号位，固定为0</li>
 *   <li>41位时间戳（毫秒），支持约69年</li>
 *   <li>5位数据中心ID（0~31）</li>
 *   <li>5位机器ID（0~31）</li>
 *   <li>12位序列号，同毫秒内最多4096个</li>
 * </ul>
 */
public class SnowflakeIdGenerator {

    private static final long EPOCH = 1704067200000L; // 2024-01-01 00:00:00 UTC

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);       // 31
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 31
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);         // 4095

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;                          // 12
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;     // 17
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS; // 22

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final SnowflakeIdGenerator INSTANCE = new SnowflakeIdGenerator(
            resolveWorkerId(), resolveDatacenterId()
    );

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId 超出范围 [0, " + MAX_WORKER_ID + "]");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId 超出范围 [0, " + MAX_DATACENTER_ID + "]");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public static long nextId() {
        return INSTANCE.generateId();
    }

    public static long getWorkerId() {
        return INSTANCE.workerId;
    }

    public static long getDatacenterId() {
        return INSTANCE.datacenterId;
    }

    /**
     * 从ID中解析出生成时间戳（毫秒）。
     */
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + EPOCH;
    }

    /**
     * 从ID中解析出数据中心ID。
     */
    public static long parseDatacenterId(long id) {
        return (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
    }

    /**
     * 从ID中解析出机器ID。
     */
    public static long parseWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * 从ID中解析出序列号。
     */
    public static long parseSequence(long id) {
        return id & SEQUENCE_MASK;
    }

    public synchronized long generateId() {
        long timestamp = currentTimeMillis();

        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = currentTimeMillis();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException("时钟回拨，拒绝生成ID，回拨时间: " + (lastTimestamp - timestamp) + "ms");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("时钟回拨等待被中断", e);
                }
            } else {
                throw new RuntimeException("时钟回拨，拒绝生成ID，回拨时间: " + offset + "ms");
            }
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private static long resolveWorkerId() {
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            long pid = Long.parseLong(name.split("@")[0]);
            return pid & MAX_WORKER_ID;
        } catch (Exception e) {
            return 0L;
        }
    }

    private static long resolveDatacenterId() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network != null) {
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    long id = ((0x000000FF & (long) mac[mac.length - 2])
                            | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
                    return id & MAX_DATACENTER_ID;
                }
            }
            return 1L;
        } catch (Exception e) {
            return 1L;
        }
    }
}
