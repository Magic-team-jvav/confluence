package org.confluence.mod.common.init;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.function.Supplier;

public final class ModBoatTypes {
    public static final EnumProxy<Boat.Type> ASH = register(() -> NatureBlocks.ASH_LOG_BLOCKS.PLANKS, "ash");
    public static final EnumProxy<Boat.Type> BAOBAB = register(() -> NatureBlocks.BAOBAB_LOG_BLOCKS.PLANKS, "baobab");
    public static final EnumProxy<Boat.Type> EBONY = register(() -> NatureBlocks.EBONY_LOG_BLOCKS.PLANKS, "ebony");
    public static final EnumProxy<Boat.Type> GLOWING_MUSHROOM = register(() -> NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.PLANKS, "glowing_mushroom");
    public static final EnumProxy<Boat.Type> LIVING = register(() -> NatureBlocks.LIVING_LOG_BLOCKS.PLANKS, "living");
    public static final EnumProxy<Boat.Type> LIVING_MAHOGANY = register(() -> NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.PLANKS, "living_mahogany");
    public static final EnumProxy<Boat.Type> PALM = register(() -> NatureBlocks.PALM_LOG_BLOCKS.PLANKS, "palm");
    public static final EnumProxy<Boat.Type> PEARL = register(() -> NatureBlocks.PEARL_LOG_BLOCKS.PLANKS, "pearl");
    public static final EnumProxy<Boat.Type> SHADOW = register(() -> NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS, "shadow");
    public static final EnumProxy<Boat.Type> SPOOKY = register(() -> NatureBlocks.SPOOKY_LOG_BLOCKS.PLANKS, "spooky");
    public static final EnumProxy<Boat.Type> YELLOW_WILLOW = register(() -> NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.PLANKS, "yellow_willow");
    public static final EnumProxy<Boat.Type> DYNASTY = register(() -> NatureBlocks.DYNASTY_LOG_BLOCKS.PLANKS, "dynasty");
    public static final EnumProxy<Boat.Type> PINE = register(() -> NatureBlocks.PINE_LOG_BLOCKS.PLANKS, "pine");
    public static final EnumProxy<Boat.Type> FEY = register(() -> NatureBlocks.FEY_LOG_BLOCKS.PLANKS, "fey");

    private static EnumProxy<Boat.Type> register(Supplier<Supplier<Block>> planks, String name) {
        return new EnumProxy<>(planks, name);
    }

    public static class EnumProxy<E extends Enum<E>> {
        public final Supplier<Supplier<Block>> planks;
        public final String name;
        private E value;

        EnumProxy(Supplier<Supplier<Block>> planks, String name) {
            this.planks = planks;
            this.name = name;
        }

        public void setValue(E value) {
            this.value = value;
        }

        public E getValue() {
            return value;
        }
    }
}
