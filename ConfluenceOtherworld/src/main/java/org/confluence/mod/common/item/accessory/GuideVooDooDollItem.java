package org.confluence.mod.common.item.accessory;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class GuideVooDooDollItem extends BaseCurioItem {
    public static final String IS_WALL_KEY = "IsWall";
    public static final String DIRECTION_KEY = "Direction";

    public GuideVooDooDollItem(String name) {
        super(builder(name).tooltips(1).rarity(ModRarity.WHITE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide) {
            LibUtils.updateItemStackNbt(stack, tag -> tag.putBoolean(IS_WALL_KEY, !isWall(tag)));
        }
        return stack;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        if (damageSource.is(DamageTypes.LAVA) && itemEntity.level() instanceof ServerLevel level) {
            CompoundTag tag = LibUtils.getItemStackNbtNoCopy(itemEntity.getItem());
            BlockPos blockPos = itemEntity.blockPosition();
            Direction direction;
            if (tag.contains(DIRECTION_KEY)) {
                // 按玩家朝向决定
                direction = Direction.CODEC.parse(NbtOps.INSTANCE, tag.get(DIRECTION_KEY)).result().orElse(Direction.WEST);
            } else {
                // 按绝对坐标决定
                int x = blockPos.getX(), z = blockPos.getZ();
                if (Math.abs(x) >= Math.abs(z)) {
                    boolean negative = x < 0;
                    for (ServerPlayer player : level.players()) {
                        int px = player.blockPosition().getX();
                        if (negative) if (px < x) x = px;
                        else if (px > x) x = px;
                    }
                    direction = Direction.fromAxisAndDirection(Direction.Axis.X, negative ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
                } else {
                    boolean negative = z < 0;
                    for (ServerPlayer player : level.players()) {
                        int pz = player.blockPosition().getZ();
                        if (negative) if (pz < z) z = pz;
                        else if (pz > x) x = pz;
                    }
                    direction = Direction.fromAxisAndDirection(Direction.Axis.Z, negative ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
                }
            }

            if (isWall(tag)) {
                WallOfFlesh wallOfFlesh = TEBossEntities.WALL_OF_FLESH.get().spawn(level, blockPos.relative(direction, 64), MobSpawnType.MOB_SUMMONED);
                if (wallOfFlesh != null) {
                    wallOfFlesh.setForward(direction.getOpposite());
                }
            } else {
                TEBossEntities.HILL_OF_FLESH.get().spawn(level, blockPos, MobSpawnType.MOB_SUMMONED);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (isWall(LibUtils.getItemStackNbtIfPresent(stack))) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.wall.0").withStyle(ChatFormatting.DARK_RED));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.wall.1").withStyle(ChatFormatting.DARK_RED));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.wall.2").withStyle(ChatFormatting.DARK_RED));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.wall.3").withStyle(ChatFormatting.DARK_RED));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.wall.4").withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.hill.0").withStyle(ChatFormatting.DARK_PURPLE));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.hill.1").withStyle(ChatFormatting.DARK_PURPLE));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.hill.2").withStyle(ChatFormatting.DARK_PURPLE));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.hill.3").withStyle(ChatFormatting.DARK_PURPLE));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.guide_voodoo_doll.hill.4").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    public static boolean isWall(@Nullable CompoundTag tag) {
        return tag == null || !tag.contains(IS_WALL_KEY) || tag.getBoolean(IS_WALL_KEY);
    }
}
