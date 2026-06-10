package org.confluence.mod.common.init.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.item.common.SpongeItem;

import java.util.stream.Stream;

public class DispenserRegistration {
    public static void boostrap() {
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
                    return super.execute(source, itemStack);
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
                    return super.execute(source, itemStack);
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
        Stream.of(
                ToolItems.BOTTOMLESS_WATER_BUCKET,
                ToolItems.BOTTOMLESS_LAVA_BUCKET,
                ToolItems.BOTTOMLESS_HONEY_BUCKET,
                ToolItems.BOTTOMLESS_SHIMMER_BUCKET
        ).forEach(item -> DispenserBlock.registerBehavior(item.asItem(), bottomlessFluidOut));

        Stream.of(
                ToolItems.SUPER_ABSORBANT_SPONGE,
                ToolItems.HONEY_ABSORBANT_SPONGE,
                ToolItems.LAVA_ABSORBANT_SPONGE,
                ToolItems.ULTRA_ABSORBANT_SPONGE
        ).forEach(item -> DispenserBlock.registerBehavior(item.asItem(), spongeAbsorbLiquid));

        BoatItems.BOAT_ITEMS.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(boatItem ->
                        DispenserBlock.registerBehavior(boatItem, new BoatDispenseItemBehavior(((BoatItem) boatItem).type))
                );
        BoatItems.CHEST_BOAT_ITEMS.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(chestBoatItem ->
                        DispenserBlock.registerBehavior(chestBoatItem, new BoatDispenseItemBehavior(((BoatItem) chestBoatItem).type, true))
                );
        ArrowItems.ITEMS.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(DispenserBlock::registerProjectileBehavior);

        DispenserBlock.registerBehavior(PotionItems.BOTTLE.asItem(), new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.level();
                BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                if (level.getFluidState(pos).is(FluidTags.WATER)) {
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                    this.setSuccess(true);
                    return this.consumeWithRemainder(source, stack, PotionItems.BOTTLED_WATER.toStack());
                }
                return super.execute(source, stack);
            }
        });
    }
}
