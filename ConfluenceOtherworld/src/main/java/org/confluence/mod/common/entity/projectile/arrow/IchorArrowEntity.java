package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

/// 灵液箭：命中时附加灵液减益（敌怪 10 秒，玩家 15 秒）。
public class IchorArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/ichor_arrow.png");
    private static final int ENEMY_TICKS = 10 * 20;
    private static final int PLAYER_TICKS = 15 * 20;

    public IchorArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public IchorArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected double getAdditionalKnockback() {
        return 1.5;
    }

    @Override
    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
        int ticks = target instanceof Player ? PLAYER_TICKS : ENEMY_TICKS;
        target.addEffect(new MobEffectInstance(ModEffects.ICHOR.get(), ticks), owner);
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
