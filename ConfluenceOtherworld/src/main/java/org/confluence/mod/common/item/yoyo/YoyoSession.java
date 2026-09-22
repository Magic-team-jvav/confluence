package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// 玩家的有绳悠悠球组；脱手弹幕不再占用下一次发射的所有权。
public final class YoyoSession implements Immunity {
    private final List<YoyoEntity> attached = new ArrayList<>(4);
    private @Nullable YoyoEntity entity;
    private ItemStack sourceStack = ItemStack.EMPTY;
    private int selectedSlot = -1;
    private boolean inputHeld;
    private float spinAge;
    private float gloveDrain = 1;
    private int specialHits;
    private YoyoEquipment equipment = new YoyoEquipment(false, false, false, 0);

    public static YoyoSession of(ServerPlayer player) {
        return player.getData(ModAttachmentTypes.YOYO_SESSION);
    }

    public boolean press(ServerPlayer player, ItemStack stack) {
        if (!(stack.getItem() instanceof YoyoItem) || !player.isAlive() || player.isSpectator())
            return false;
        boolean sameSource = selectedSlot == player.getInventory().selected && sourceStack == stack;
        if (!sameSource) retract();
        inputHeld = true;
        selectedSlot = player.getInventory().selected;
        sourceStack = stack;
        if (entity == null || !entity.isAlive()) return spawn(player);
        return false;
    }

    public void release(ServerPlayer player) {
        inputHeld = false;
        if (!player.isAlive() || player.isSpectator() || !isSourceSelected(player)) retract();
        else finishCast(equipment.offstring());
    }

    public void adjustRange(ServerPlayer player, int amount) {
        if (inputHeld && isSourceSelected(player) && entity != null && entity.isAlive())
            entity.adjustRange(amount);
    }

    public void tick(ServerPlayer player) {
        attached.removeIf(part -> !part.isAlive());
        if (!player.isAlive() || player.isSpectator() || !isSourceSelected(player)) {
            inputHeld = false;
            retract();
            sourceStack = ItemStack.EMPTY;
            selectedSlot = -1;
            return;
        }
        if (entity != null && !entity.isAlive()) retract();
        if (entity != null && !entity.isReturning()) {
            spinAge += gloveDrain;
            int lifetime = equipment.lifetime(((YoyoItem) sourceStack.getItem()).lifetimeTicks());
            if (lifetime != 0 && spinAge >= lifetime) finishCast(equipment.offstring());
        }
        if (entity == null && inputHeld) spawn(player);
    }

    public boolean owns(YoyoEntity candidate, ServerPlayer player) {
        return attached.contains(candidate) && isSourceSelected(player);
    }

    /// 手套优先生成副球，然后逐次生成两个平衡锤；平衡锤本身不能触发武器特效。
    public void onHit(ServerPlayer owner, YoyoEntity source) {
        if (entity == null || source.isDetached() || source.isReturning() || source.isCounterweight() || !attached.contains(source))
            return;
        YoyoEntity.Role role;
        if (equipment.glove() && !hasRole(YoyoEntity.Role.DUPLICATE)) {
            role = YoyoEntity.Role.DUPLICATE;
        } else if (equipment.counterweight() != 0 && !hasRole(YoyoEntity.Role.COUNTERWEIGHT)) {
            role = YoyoEntity.Role.COUNTERWEIGHT;
        } else if (equipment.glove() && equipment.counterweight() != 0 && !hasRole(YoyoEntity.Role.SECOND_COUNTERWEIGHT)) {
            role = YoyoEntity.Role.SECOND_COUNTERWEIGHT;
        } else return;
        YoyoEntity part = YoyoEntity.spawnCompanion(owner, entity, role, equipment);
        if (part != null) {
            attached.add(part);
            if (role == YoyoEntity.Role.DUPLICATE)
                gloveDrain = 2 + owner.getRandom().nextFloat() * 2;
        }
    }

    private boolean hasRole(YoyoEntity.Role role) {
        return attached.stream().anyMatch(part -> part.getRole() == role && part.isAlive());
    }

    /// 四种连击悠悠球共用玩家计数；收回、换球不会重置，平衡锤不参与。
    /// 伤害计算和命中特效都在递增前查询，因此判断即将发生的这次命中。
    public boolean isSpecialHit(int period) {return (specialHits + 1) % period == 0;}

    public void countSpecialHit() {++specialHits;}

    /// 普通命中消耗三分之一秒，球与玩家之间隔着实体方块时消耗一秒。
    public void consumeSpin(boolean obstructed) {spinAge += obstructed ? 20 : 20.0F / 3;}

    private boolean isSourceSelected(ServerPlayer player) {
        return selectedSlot == player.getInventory().selected && sourceStack == player.getMainHandItem() && sourceStack.getItem() instanceof YoyoItem;
    }

    private boolean spawn(ServerPlayer player) {
        equipment = YoyoEquipment.of(player);
        entity = YoyoEntity.spawn(player, sourceStack, equipment);
        spinAge = 0;
        gloveDrain = 1;
        if (entity != null) attached.add(entity);
        return entity != null;
    }

    /// 非主动松手的物品切换不产生免费脱手弹幕。
    private void retract() {
        for (YoyoEntity part : attached) part.beginReturn();
        attached.clear();
        entity = null;
    }

    private void finishCast(boolean detach) {
        if (!detach) {
            for (YoyoEntity part : attached) part.beginReturn();
            return;
        }
        for (YoyoEntity part : attached) part.detach();
        attached.clear();
        entity = null;
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    /// 同一玩家的主球、副球和平衡锤共用命中间隔，避免每增加一个球就成倍叠伤。
    @Override
    public int confluence$getImmunityDuration(DamageSource source) {
        return source.getDirectEntity() instanceof YoyoEntity yoyo ? yoyo.confluence$getImmunityDuration(source) : 5;
    }
}
