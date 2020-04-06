package com.jgfx.engine.utils;

import com.google.common.annotations.Beta;


/**
 * Contains a lot of useful math functions for the game engine
 */
public final class JMath {
    private static final int MAX_SIGNED_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    /**
     * Returns {@code true} if {@code x} represents a power of two.
     *
     * <p>This differs from {@code Integer.bitCount(x) == 1}, because
     * {@code Integer.bitCount(Integer.MIN_VALUE) == 1}, but {@link Integer#MIN_VALUE} is not a power
     * of two.
     */
    public static boolean isPowerOfTwo(int x) {
        return x > 0 & (x & (x - 1)) == 0;
    }

    /**
     * Returns the smallest power of two greater than or equal to {@code x}.  This is equivalent to
     * {@code checkedPow(2, log2(x, CEILING))}.
     *
     * @throws IllegalArgumentException if {@code x <= 0}
     * @throws ArithmeticException      of the next-higher power of two is not representable as an
     *                                  {@code int}, i.e. when {@code x > 2^30}
     * @since 20.0
     */
    @Beta
    public static int ceilingPowerOfTwo(int x) {
        if (x > MAX_SIGNED_POWER_OF_TWO) {
            throw new ArithmeticException("ceilingPowerOfTwo(" + x + ") not representable as an int");
        }
        return 1 << -Integer.numberOfLeadingZeros(x - 1);
    }

    /**
     * Returns the largest power of two less than or equal to {@code x}.  This is equivalent to
     * {@code checkedPow(2, log2(x, FLOOR))}.
     *
     * @throws IllegalArgumentException if {@code x <= 0}
     * @since 20.0
     */
    @Beta
    public static int floorPowerOfTwo(int x) {
        return Integer.highestOneBit(x);
    }

    /**
     * @param value
     * @return The size of a power of two - that is, the exponent.
     */
    @Beta
    public static int sizeOfPower(int value) {
        int power = 0;
        int val = value;
        while (val > 1) {
            val = val >> 1;
            power++;
        }
        return power;
    }
}
