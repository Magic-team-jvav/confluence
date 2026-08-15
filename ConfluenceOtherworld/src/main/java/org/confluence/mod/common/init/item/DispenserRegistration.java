package org.confluence.mod.common.init.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.confluence.mod.common.item.common.SpongeItem;

import java.util.stream.Stream;

public class DispenserRegistration {
    private static final DefaultDispenseItemBehavior REMAINDER_DISPENSER = new DefaultDispenseItemBehavior();

    /**
     * 注册本模组液体工具在发射器中的交互行为。
     * <p>原版已经处理普通箭、船等常规物品，这里只补本模组新增的桶、无底桶、吸水海绵和空瓶。</p>
     */
    public static void boostrap() {
        DispenseItemBehavior bucketEmptying = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                DispensibleContainerItem containerItem = (DispensibleContainerItem) stack.getItem();
                BlockPos targetPos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                Level level = source.getLevel();
                if (containerItem.emptyContents(null, level, targetPos, null, stack)) {
                    containerItem.checkExtraContent(null, level, stack, targetPos);
                    return consumeWithRemainder(source, stack, new ItemStack(Items.BUCKET));
                }
                return super.execute(source, stack);
            }
        };

        DispenseItemBehavior bottomlessBucketEmptying = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                DispensibleContainerItem containerItem = (DispensibleContainerItem) stack.getItem();
                BlockPos targetPos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                Level level = source.getLevel();
                if (containerItem.emptyContents(null, level, targetPos, null, stack)) {
                    containerItem.checkExtraContent(null, level, stack, targetPos);
                    return stack;
                }
                return super.execute(source, stack);
            }
        };

        DispenseItemBehavior spongeAbsorbing = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                SpongeItem spongeItem = (SpongeItem) stack.getItem();
                BlockPos targetPos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                if (spongeItem.tryAbsorbLiquid(source.getLevel(), targetPos)) {
                    source.getLevel().gameEvent(null, GameEvent.BLOCK_CHANGE, targetPos);
                }
                return stack;
            }
        };

        DispenserBlock.registerBehavior(ToolItems.HONEY_BUCKET.get(), bucketEmptying);
        Stream.of(
                ToolItems.BOTTOMLESS_WATER_BUCKET,
                ToolItems.BOTTOMLESS_LAVA_BUCKET,
                ToolItems.BOTTOMLESS_HONEY_BUCKET,
                ToolItems.BOTTOMLESS_SHIMMER_BUCKET
        ).forEach(item -> DispenserBlock.registerBehavior(item.get(), bottomlessBucketEmptying));

        Stream.of(
                ToolItems.SUPER_ABSORBANT_SPONGE,
                ToolItems.HONEY_ABSORBANT_SPONGE,
                ToolItems.LAVA_ABSORBANT_SPONGE,
                ToolItems.ULTRA_ABSORBANT_SPONGE
        ).forEach(item -> DispenserBlock.registerBehavior(item.get(), spongeAbsorbing));

        DispenserBlock.registerBehavior(PotionItems.BOTTLE.get(), new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                BlockPos targetPos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                if (source.getLevel().getFluidState(targetPos).is(FluidTags.WATER)) {
                    source.getLevel().gameEvent(null, GameEvent.FLUID_PICKUP, targetPos);
                    setSuccess(true);
                    return consumeWithRemainder(source, stack, PotionItems.BOTTLED_WATER.toStack());
                }
                return super.execute(source, stack);
            }
        });
    }

    /**
     * 按原版发射器的容器物品规则处理剩余物。
     * <p>物品堆耗尽时直接返回剩余物；没有耗尽时优先塞回发射器，容器满了再把剩余物发射出去。</p>
     */
    private static ItemStack consumeWithRemainder(BlockSource source, ItemStack stack, ItemStack remainder) {
        stack.shrink(1);
        if (stack.isEmpty()) {
            return remainder;
        }
        if (source.getEntity() instanceof DispenserBlockEntity dispenser && dispenser.addItem(remainder) < 0) {
            REMAINDER_DISPENSER.dispense(source, remainder);
        }
        return stack;
    }
}
