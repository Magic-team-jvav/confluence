package org.confluence.mod.util;

public final class BlockCounts {
    public short crimson;
    public short crimsonSand;
    public short crimsonIce;
    public short corrupt;
    public short corruptSand;
    public short corruptIce;
    public short hallow;
    public short hallowSand;
    public short hallowIce;
    public short sunflower;
    public short glowing_mushroom;
    public short tomb;
    public short water;
    public short chlorophyte;

    public boolean isGraveyard() {
        return tomb - sunflower >= 7;
    }

    @Override
    public String toString() {
        return "BlockCounts{" +
                "crimson=" + crimson +
                ", crimsonSand=" + crimsonSand +
                ", crimsonIce=" + crimsonIce +
                ", corrupt=" + corrupt +
                ", corruptSand=" + corruptSand +
                ", corruptIce=" + corruptIce +
                ", hallow=" + hallow +
                ", hallowSand=" + hallowSand +
                ", hallowIce=" + hallowIce +
                ", sunflower=" + sunflower +
                ", glowing_mushroom=" + glowing_mushroom +
                ", tomb=" + tomb +
                ", water=" + water +
                ", chlorophyte=" + chlorophyte +
                '}';
    }
}
