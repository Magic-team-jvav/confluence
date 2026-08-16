package org.confluence.mod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.entity.monster.BaseMimic;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.gun.definition.BulletBehavior;
import org.confluence.mod.common.item.gun.definition.BulletDefinition;
import org.confluence.mod.common.item.gun.definition.BulletImpactEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseBullet extends Item {
    private final BulletDefinition definition;
    private final BulletPropertyComponent component;
    protected String colorID = "";

    public BaseBullet(Properties properties, float damage, float velocity, float velocityMultiplier, float knockback, ModRarity rarity, int penetrate, boolean infinity) {
        this(properties, new BulletDefinition(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity));
    }

    public BaseBullet(Properties properties, BulletDefinition definition) {
        super(setup(properties, definition));
        this.definition = definition;
        this.component = definition.component();
    }

    private static Properties setup(Properties properties, BulletDefinition definition) {
        properties.component(ModDataComponentTypes.BULLET_PROPERTY, definition.component());
        // 1.21 的 TerraGuns 在独立运行时以 99 为基础栈上限，
        // 但被 Confluence 加载时会把普通弹药扩展到 9999；1.20 侧是合并态，直接使用合并后的上限。
        return properties.stacksTo(definition.infinity() ? 1 : 9999);
    }

    public void tick(BaseBulletEntity baseBulletEntity) {
        definition.behavior().tick(baseBulletEntity);
    }

    public void onHitBlock(BaseBulletEntity bulletEntity, BlockHitResult result) {
        if (!definition.behavior().onHitBlock(bulletEntity, result)) {
            bulletEntity.discard();
        }
    }

    /// 执行子弹物品的实际伤害。
    ///
    /// @return 目标确实接受伤害时为 {@code true}；只有此时实体才记录 UUID、击退并消耗穿透
    public boolean onHitEntity(BaseBulletEntity bulletEntity, EntityHitResult result) {
        boolean damaged = result.getEntity().hurt(bulletEntity.getDamageSource(), bulletEntity.getCalculatedDamage());
        // 被困难宝箱怪反射的弹丸只保留 50% 直接伤害，不再携带原弹药的减益或分裂效果。
        if (damaged && !(bulletEntity.getOwner() instanceof BaseMimic)) {
            definition.behavior().onHitEntity(bulletEntity, result);
        }
        return damaged;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.confluence.ranged_damage", component.damage()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.knockback", component.knockback()).withStyle(ChatFormatting.GRAY));
        String abilityTooltip = definition.behavior().tooltipKey();
        if (!abilityTooltip.isEmpty()) {
            tooltipComponents.add(Component.translatable(abilityTooltip).withStyle(ChatFormatting.AQUA));
        }
    }

    public @Nullable String colorID() {
        return colorID;
    }

    public BulletDefinition getDefinition() {
        return definition;
    }

    public BulletBehavior getBehavior() {
        return definition.behavior();
    }

    public BulletImpactEffect getImpactEffect() {
        return definition.impactEffect();
    }

    public static class Dummy extends BaseBullet {
        public Dummy(Properties properties) {
            super(properties, 0, 0, 0, 0, ModRarity.WHITE, 0, false);
        }

        void colorID(String colorID) {
            this.colorID = colorID;
        }
    }
}
