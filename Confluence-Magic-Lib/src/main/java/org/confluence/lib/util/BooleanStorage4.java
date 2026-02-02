package org.confluence.lib.util;

import it.unimi.dsi.fastutil.booleans.BooleanUnaryOperator;

public class BooleanStorage4 {
    private byte value;

    public BooleanStorage4(byte value) {
        this.value = value;
    }

    public BooleanStorage4() {
        this.value = 0b0000;
    }

    public void set(int index, boolean b) {
        if (index < 0 || index > 3) throw new IndexOutOfBoundsException("Index must be between 0 and 3");

        if (b) value |= (byte) (1 << index);
        else value &= (byte) ~(1 << index);
    }

    public void set(byte value) {
        this.value = value;
    }

    public boolean get(int index) {
        if (index < 0 || index > 3) throw new IndexOutOfBoundsException("Index must be between 0 and 3");

        return (value & (1 << index)) != 0;
    }

    public byte getValue() {
        return value;
    }

    public boolean matches(byte value) {
        return this.value == value;
    }

    public BooleanStorage4 copy() {
        return new BooleanStorage4(value);
    }

    public int size() {
        return 4;
    }

    public BooleanStorage4 map(BooleanUnaryOperator operator) {
        byte n = 0b0000;
        if (operator.apply((value & 0b0001) != 0)) n |= 0b0001;
        if (operator.apply((value & 0b0010) != 0)) n |= 0b0010;
        if (operator.apply((value & 0b0100) != 0)) n |= 0b0100;
        if (operator.apply((value & 0b1000) != 0)) n |= 0b1000;
        return new BooleanStorage4(n);
    }
}
