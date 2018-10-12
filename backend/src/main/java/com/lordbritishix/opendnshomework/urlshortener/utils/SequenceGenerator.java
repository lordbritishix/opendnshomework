package com.lordbritishix.opendnshomework.urlshortener.utils;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copied from https://www.callicoder.com/distributed-unique-id-sequence-number-generator/
 *
 * tldr: sequence generator that's 64-bit long and uses epoch time, machine id, and some machine counter to help generate
 * unique id that's useful for distributed systems or clusters of services.
 */
public final class SequenceGenerator {
    private static final AtomicInteger counter = new AtomicInteger(new SecureRandom().nextInt());

    private static final int TOTAL_BITS = 64;
    private static final int EPOCH_BITS = 42;
    private static final int MACHINE_ID_BITS = 10;

    private static final int MACHINE_ID;
    private static final int LOWER_ORDER_TEN_BITS = 0x3FF;
    private static final int LOWER_ORDER_TWELVE_BITS = 0xFFF;

    public static long nextId() {
        long curMs = Instant.now().toEpochMilli();
        long id = curMs << (TOTAL_BITS - EPOCH_BITS);
        id |= (MACHINE_ID << (TOTAL_BITS - EPOCH_BITS - MACHINE_ID_BITS));
        id |= (getNextCounter() & LOWER_ORDER_TWELVE_BITS);
        return id;
    }

    private static int getNextCounter() {
        return counter.getAndIncrement();
    }

    static {
        MACHINE_ID = createMachineId();
    }

    private static int createMachineId() {
        int machineId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte aMac : mac) {
                        sb.append(String.format("%02X", aMac));
                    }
                }
            }
            machineId = sb.toString().hashCode();
        } catch (Exception ex) {
            machineId = (new SecureRandom().nextInt());
        }
        machineId = machineId & LOWER_ORDER_TEN_BITS;
        return machineId;
    }
}