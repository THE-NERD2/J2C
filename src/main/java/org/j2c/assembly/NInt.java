package org.j2c.assembly;

import org.jetbrains.annotations.NotNull;

public class NInt implements Node {
    private final int value;
    public NInt(int value) {
        this.value = value;
    }
    @Override
    @NotNull
    public String toString() {
        return String.valueOf(value);
    }
}