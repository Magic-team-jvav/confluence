package org.confluence.mod.common.summoner.summonMark;

public class SummonMarkInstance {

    private final SummonMarkType type;
    private int duration;
    private boolean used = false;

    public SummonMarkInstance(SummonMarkType type, int duration) {
        this.type = type;
        this.duration = duration;
    }

    public SummonMarkType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
