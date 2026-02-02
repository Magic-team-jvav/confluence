package org.confluence.terraentity.entity.monster.demoneye;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum DemonEyeVariant implements StringRepresentable {
    NORMAL(0, "normal", false, 15.0, 3.5, 1),
    NORMAL_BIG(1, "normal_big", true, 12.0, 4.0, 2),
    CATARACT(2, "cataract", false, 11.5, 3.5, 2),
    CATARACT_BIG(3, "cataract_big", true, 14.0, 4.0, 2),
    SLEEPY(4, "sleepy", false, 15.0, 3.0, 1),
    SLEEPY_BIG(5, "sleepy_big", true, 16.0, 3.5, 1),
    DILATED(6, "dilated", true, 12.0, 3.5, 1),
    DILATED_SMALL(7, "dilated_small", false, 11.5, 3.0, 0),
    GREEN(8, "green", true, 15.0, 4.0, 0),
    GREEN_SMALL(9, "green_small", false, 12.5, 3.0, 0),
    PURPLE(10, "purple", false, 15.0, 3.0, 2),
    PURPLE_BIG(11, "purple_big", true, 16.0, 3.0, 2),

    OWL(12, "owl", false, 18.5, 3.0, 3),
    SPACESHIP(13, "spaceship", false, 15.0, 3.0, 2);

    private static final IntFunction<DemonEyeVariant> BY_ID = ByIdMap.continuous(DemonEyeVariant::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
    final int id;
    private final String name;
    public final boolean big;
    public final double health;
    public final double damage;
    public final int armor;

    DemonEyeVariant(int pId, String pName, boolean big, double health, double damage, int armor) {
        this.id = pId;
        this.name = pName;
        this.big = big;
        this.health = health;
        this.damage = damage;
        this.armor = armor;
    }

    public int getId() {
        return id;
    }

    public static DemonEyeVariant byId(int pId) {
        return BY_ID.apply(pId);
    }

    public @NotNull String getSerializedName() {
        return name;
    }

    public int getTextureIndex() {
        int ordinal = ordinal();
        return switch (ordinal) {
            case 0, 1 -> 0;
            case 2, 3 -> 1;
            case 4, 5 -> 2;
            case 6, 7 -> 3;
            case 8, 9 -> 4;
            case 10, 11 -> 5;
            default -> ordinal - 6;
        };
    }

    public static DemonEyeVariant random(RandomSource randomSource) {
        if (TEUtils.isHalloween()) {
            return byId(randomSource.nextBoolean() ? 12 : 13);
        }
        return byId(randomSource.nextInt(12)); // 0 ~ 11
    }
}
