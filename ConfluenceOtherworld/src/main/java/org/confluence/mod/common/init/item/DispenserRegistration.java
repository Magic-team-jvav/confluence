package org.confluence.mod.common.init.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.item.common.SpongeItem;

public class DispenserRegistration {
    public static void boostrap() {
        DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();
        DispenseItemBehavior simpleFluidOut = new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource source, ItemStack itemStack) {
                DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem) itemStack.getItem();
                BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                Level level = source.level();
                if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, itemStack)) {
                    dispensiblecontaineritem.checkExtraContent(null, level, itemStack, blockpos);
                    return this.consumeWithRemainder(source, itemStack, new ItemStack(Items.BUCKET));
                } else {
                    return defaultBehavior.dispense(source, itemStack);
                }
            }
        };
        DispenseItemBehavior bottomlessFluidOut = new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource source, ItemStack itemStack) {
                DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem) itemStack.getItem();
                BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                Level level = source.level();
                if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, itemStack)) {
                    dispensiblecontaineritem.checkExtraContent(null, level, itemStack, blockpos);
                    return itemStack;
                } else {
                    return defaultBehavior.dispense(source, itemStack);
                }
            }
        };
        DispenseItemBehavior spongeAbsorbLiquid = new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource source, ItemStack itemStack) {
                SpongeItem spongeItem = (SpongeItem) itemStack.getItem();
                BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                Level level = source.level();
                spongeItem.tryAbsorbLiquid(level, blockpos);
                return itemStack;
            }
        };
        DispenserBlock.registerBehavior(ToolItems.HONEY_BUCKET.asItem(), simpleFluidOut);
        DispenserBlock.registerBehavior(ToolItems.BOTTOMLESS_WATER_BUCKET.asItem(), bottomlessFluidOut);
        DispenserBlock.registerBehavior(ToolItems.BOTTOMLESS_LAVA_BUCKET.asItem(), bottomlessFluidOut);
        DispenserBlock.registerBehavior(ToolItems.BOTTOMLESS_HONEY_BUCKET.asItem(), bottomlessFluidOut);
        DispenserBlock.registerBehavior(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.asItem(), bottomlessFluidOut);

        DispenserBlock.registerBehavior(ToolItems.SUPER_ABSORBANT_SPONGE.asItem(), spongeAbsorbLiquid);
        DispenserBlock.registerBehavior(ToolItems.HONEY_ABSORBANT_SPONGE.asItem(), spongeAbsorbLiquid);
        DispenserBlock.registerBehavior(ToolItems.LAVA_ABSORBANT_SPONGE.asItem(), spongeAbsorbLiquid);
        DispenserBlock.registerBehavior(ToolItems.ULTRA_ABSORBANT_SPONGE.asItem(), spongeAbsorbLiquid);

        BoatItems.BOAT_ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .forEach(boatItem ->
                        DispenserBlock.registerBehavior(boatItem, new BoatDispenseItemBehavior(((BoatItem) boatItem).type))
                );
        BoatItems.CHEST_BOAT_ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .forEach(chestBoatItem ->
                        DispenserBlock.registerBehavior(chestBoatItem, new BoatDispenseItemBehavior(((BoatItem) chestBoatItem).type, true))
                );
        ArrowItems.ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .forEach(DispenserBlock::registerProjectileBehavior);

        DispenserBlock.registerBehavior(PotionItems.BOTTLE.asItem(), new OptionalDispenseItemBehavior() {
            private ItemStack takeLiquid(BlockSource source, ItemStack emptyItem, ItemStack fullItem) {
                source.level().gameEvent(null, GameEvent.FLUID_PICKUP, source.pos());
                return this.consumeWithRemainder(source, emptyItem, fullItem);
            }

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack item) {
                this.setSuccess(false);
                ServerLevel serverlevel = blockSource.level();
                BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
//                BlockState blockstate = serverlevel.getBlockState(blockpos);

                // 蜂蜜 看下是否需要
//                if (blockstate.is(BlockTags.BEEHIVES, p_338544_ -> p_338544_.hasProperty(BeehiveBlock.HONEY_LEVEL) &&
//                        p_338544_.getBlock() instanceof BeehiveBlock) &&
//                        blockstate.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
//                    ((BeehiveBlock) blockstate.getBlock()).releaseBeesAndResetHoneyLevel(serverlevel, blockstate, blockpos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
//                    this.setSuccess(true);
//                    return this.takeLiquid(blockSource, item, new ItemStack(Items.HONEY_BOTTLE));
//                }

                if (serverlevel.getFluidState(blockpos).is(FluidTags.WATER)) {
                    this.setSuccess(true);
                    return this.takeLiquid(blockSource, item, PotionItems.BOTTLED_WATER.toStack());
                }

                return super.execute(blockSource, item);
            }
        });
    }
}
