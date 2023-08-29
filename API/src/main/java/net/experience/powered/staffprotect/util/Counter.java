package net.experience.powered.staffprotect.util;

public class Counter {

    private int count;

    public Counter() {
        this.count = 1;
    }

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return String.valueOf(count);
    }
}
