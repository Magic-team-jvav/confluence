package org.confluence.mod.api.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.soul.skill.SoulSkillStack;
import org.jetbrains.annotations.Nullable;

/// 服务端支付前修正；取消时不消耗资源，也不执行效果。生命代偿不经过普通受伤流程。
public class SoulConsumeEvent extends PlayerEvent implements ICancellableEvent {
    private static final float DEFAULT_HEALTH_PER_SOUL = 4.0F;
    private final ItemStack itemStack;
    private final @Nullable SoulSkillStack skillStack;
    private float soulCost;
    private float healthPerSoul = DEFAULT_HEALTH_PER_SOUL;
    private float minimumHealth;

    public SoulConsumeEvent(ServerPlayer player, ItemStack itemStack, @Nullable SoulSkillStack skillStack, float soulCost) {
        super(player);
        this.itemStack = itemStack;
        this.skillStack = skillStack;
        this.soulCost = soulCost;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public @Nullable SoulSkillStack getSkillStack() {
        return skillStack;
    }

    public float getSoulCost() {
        return soulCost;
    }

    public void setSoulCost(float soulCost) {
        this.soulCost = soulCost;
    }

    public float getHealthPerSoul() {
        return healthPerSoul;
    }

    public void setHealthPerSoul(float healthPerSoul) {
        this.healthPerSoul = healthPerSoul;
    }

    /// 默认不保留生命；专用免死效果可设置正值，不会因此回复生命。
    public float getMinimumHealth() {
        return minimumHealth;
    }

    public void setMinimumHealth(float minimumHealth) {
        this.minimumHealth = minimumHealth;
    }
}
