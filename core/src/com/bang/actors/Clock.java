package com.bang.actors;

import java.util.ArrayList;

public class Clock {

    protected int[] clock;
    protected int owner;

    public Clock(int length, int owner) {
        this.clock = new int[length];
        this.owner = owner;
    }

    public String toString() {
        String s = "[";

        for (int i : clock) {
            s = s + " " + i + " ";
        }

        s = s + "]";
        return s;
    }

    public int[] getVec() {
        return this.clock;
    }

    public int getClockByIndex(int i) {
        return this.clock[i];
    }

    protected void clockIncreaseLocal() {
        this.clock[this.owner]++;
    }

    protected void clockIncrease(int[] otherClock) {
        for (int i = 0; i < this.clock.length; i++) {
            this.clock[i] = Math.max(this.clock[i], otherClock[i]);
            if (i == this.owner)
                this.clock[i]++;
        }

    }

    protected int clockCompare(int[] clock1, int[] clock2) { //1 if clock1 > clock2, 2 if clock2 > clock1, 0 else 
        Boolean found = true;
        for (int i = 0; i < clock1.length; i++) {
            if (clock1[i] < clock2[i]) {
                found = false;
                break;
            }
        }
        if (found)
            return 1;
        found = true;
        for (int i = 0; i < clock1.length; i++) {
            if (clock2[i] < clock1[i]) {
                found = false;
                break;
            }
        }
        if (found)
            return 2;
        return 0;
    }

    protected Boolean cutConsistencyCheck(ArrayList<Clock> cut) {
        for (int i = 0; i < cut.size(); i++) {
            int localVal = cut.get(i).getVec()[i]; // i-esima posizione dell'i-esimo orologio
            for (int j = 0; j < cut.size(); j++) {
                if (cut.get(j).getVec()[i] > localVal) { //il valore i-esimo del proce i-esimo deve essere >= del valore i-esimo di ogni altro proc.
                    return false;
                }
            }
        }
        return true;
    }

}