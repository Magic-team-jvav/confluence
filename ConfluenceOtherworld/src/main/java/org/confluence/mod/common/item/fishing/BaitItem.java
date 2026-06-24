package org.confluence.mod.common.item.fishing;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaitItem extends Item implements IBait {
    private final float bonus;
    private final @Nullable Supplier<? extends EntityType<?>> supplier;
    private final Consumer<Entity> consumer;

    public BaitItem(ModRarity rarity, float bonus, @Nullable Supplier<? extends EntityType<?>> supplier, Consumer<Entity> consumer) {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity).stacksTo(9999));
        this.bonus = bonus;
        this.supplier = supplier;
        this.consumer = consumer;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (supplier != null) {
            if (!(context.getLevel() instanceof ServerLevel level)) {
                return InteractionResult.SUCCESS;
            }

            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            if (level.getBlockEntity(blockpos) instanceof SpawnerBlockEntity spawner) {
                EntityType<?> entitytype1 = supplier.get();
                spawner.setEntityId(entitytype1, level.getRandom());
                level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
                itemstack.shrink(1);
            } else {
                BlockPos blockpos1;
                if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                    blockpos1 = blockpos;
                } else {
                    blockpos1 = blockpos.relative(direction);
                }

                EntityType<?> entitytype = supplier.get();
                Entity entity = entitytype.spawn(
                        level,
                        itemstack,
                        context.getPlayer(),
                        blockpos1,
                        MobSpawnType.SPAWN_EGG,
                        true,
                        !Objects.equals(blockpos, blockpos1) && direction == Direction.UP
                );
                if (entity != null) {
                    consumer.accept(entity);
                    itemstack.shrink(1);
                    level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
                }
            }
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (supplier != null) {
            ItemStack stack = player.getItemInHand(usedHand);
            BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (blockhitresult.getType() != HitResult.Type.BLOCK) {
                return InteractionResultHolder.pass(stack);
            }
            if (!(level instanceof ServerLevel)) return InteractionResultHolder.success(stack);

            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!(level.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(stack);
            }

            if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, blockhitresult.getDirection(), stack)) {
                EntityType<?> entitytype = supplier.get();
                Entity entity = entitytype.spawn((ServerLevel) level, stack, player, blockpos, MobSpawnType.SPAWN_EGG, false, false);
                if (entity == null) return InteractionResultHolder.pass(stack);

                consumer.accept(entity);
                stack.consume(1, player);
                player.awardStat(Stats.ITEM_USED.get(this));
                level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.fail(stack);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public float getBaitBonus() {
        return bonus;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.bait.common.0")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
                "info.confluence.bait",
                ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(getBaitBonus() * 100.0)
        ).withStyle(style -> style.withColor(ChatFormatting.BLUE)));
    }
}
