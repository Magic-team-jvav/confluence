package org.confluence.mod.common.init;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import org.confluence.lib.common.fluid.FluidTriple;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.ToolItems;

import static org.confluence.mod.api.event.ShimmerEntityTransmutationEvent.addEntity;

public final class ModFluids {
    public static final FluidTriple HONEY = FluidTriple.builder(Confluence.asResource("honey"))
            .typeProperties(properties -> properties
                    .density(2000)
                    .canSwim(false)
                    .viscosity(3000)
                    .motionScale(0.0003)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .rarity(Rarity.UNCOMMON)
                    .fallDistanceModifier(0.2F)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .addDripstoneDripping(
                            PointedDripstoneBlock.LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK,
                            ParticleTypes.DRIPPING_DRIPSTONE_WATER,
                            ModBlocks.HONEY_CAULDRON.get(),
                            SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON
                    )
            ).baseProperties(properties -> properties
                    .block(ModBlocks.HONEY)
                    .bucket(ToolItems.HONEY_BUCKET)
            ).build();
    public static final FluidTriple SHIMMER = FluidTriple.builder(Confluence.asResource("shimmer"))
            .typeProperties(properties -> properties
                    .density(800)
                    .lightLevel(10)
                    .viscosity(800)
                    .canSwim(false)
                    .motionScale(0.02)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .rarity(Rarity.EPIC)
                    .fallDistanceModifier(0.0F)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .addDripstoneDripping(
                            PointedDripstoneBlock.LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK,
                            ParticleTypes.DRIPPING_DRIPSTONE_WATER,
                            ModBlocks.AETHERIUM_CAULDRON.get(),
                            SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON
                    )
            ).baseProperties(properties -> properties
                    .block(ModBlocks.SHIMMER)
                    .bucket(() -> Items.AIR)
            ).build();

    public static void registerInteraction() {
        FluidInteractionRegistry.addInteraction(HONEY.type().get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.WATER_TYPE.value(), fluidState -> fluidState.isSource() ? Blocks.HONEY_BLOCK.defaultBlockState() : NatureBlocks.THIN_HONEY_BLOCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(HONEY.type().get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.LAVA_TYPE.value(), fluidState -> fluidState.isSource() ? DecorativeBlocks.CRISPY_HONEY_BLOCK.get().defaultBlockState() : NatureBlocks.LOOSE_HONEY_BLOCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(SHIMMER.type().get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.WATER_TYPE.value(), fluidState -> fluidState.isSource() ? NatureBlocks.AETHERIUM_BLOCK.get().defaultBlockState() : NatureBlocks.DARK_AETHERIUM_BLOCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(SHIMMER.type().get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.LAVA_TYPE.value(), fluidState -> fluidState.isSource() ? NatureBlocks.AETHERIUM_BLOCK.get().defaultBlockState() : NatureBlocks.DARK_AETHERIUM_BLOCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(SHIMMER.type().get(), new FluidInteractionRegistry.InteractionInformation(
                HONEY.type().get(), fluidState -> fluidState.isSource() ? NatureBlocks.AETHERIUM_BLOCK.get().defaultBlockState() : NatureBlocks.DARK_AETHERIUM_BLOCK.get().defaultBlockState()
        ));
    }

    public static void registerShimmerTransform() {
        addEntity(EntityType.WITCH, EntityType.VILLAGER);
        addEntity(entity -> {
            EntityType<?> entityType = entity.getType();
            return entityType == EntityType.PIGLIN ||
                    entityType == EntityType.PIGLIN_BRUTE ||
                    entityType == EntityType.ZOMBIFIED_PIGLIN ||
                    entityType == EntityType.CREEPER;
        }, EntityType.PIG);
        addEntity(entity -> entity instanceof AbstractSkeleton || entity.getType() == EntityType.ZOMBIE, EntityType.SKELETON);
        addEntity(entity -> entity instanceof AbstractHorse, EntityType.HORSE);
        addEntity(EntityType.VEX, EntityType.ALLAY);
        addEntity(entity -> entity instanceof Creeper creeper && creeper.isPowered(), EntityType.CREEPER);
        addEntity(EntityType.MOOSHROOM, EntityType.COW);
    }

    public static void initialize() {}
}
