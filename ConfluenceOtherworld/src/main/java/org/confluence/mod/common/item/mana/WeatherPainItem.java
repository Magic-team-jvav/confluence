package org.confluence.mod.common.item.mana;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.HurtnadoProjectile;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.Optional;

/// 保留向上初速度，并在新龙卷风生成成功后替换旧实体的天气之痛。
public class WeatherPainItem extends ManaStaffItem<HurtnadoProjectile> {
    private TooltipComponent component;

    public WeatherPainItem() {
        super(ModRarity.GREEN, HurtnadoProjectile::new, 6.5F, 30, 1.0F, 45, builder -> builder
                .add(LibAttributes.getCriticalChance(),
                        new PortAttributeModifier(ID, 0.04, PortAttributeModifier.Operation.ADD_VALUE),
                        PortEquipmentSlotGroup.MAINHAND)
                .add(LibAttributes.getArmorPenetration(),
                        new PortAttributeModifier(ID, 10, PortAttributeModifier.Operation.ADD_VALUE),
                        PortEquipmentSlotGroup.MAINHAND));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(component);
    }

    /// 返回旧实现“视角速度加 0.4Y”得到的实际运动方向。
    @Override
    protected Vec3 launchDirection(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            HurtnadoProjectile projectile
    ) {
        return weatherMotion(context, snapshot);
    }

    /// 用单枚速度倍率保留附加 Y 速度造成的总速度变化。
    @Override
    protected float velocityMultiplier(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            HurtnadoProjectile projectile
    ) {
        return (float) (weatherMotion(context, snapshot).length() / snapshot.resolvedVelocity());
    }

    private static Vec3 weatherMotion(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot
    ) {
        return context.viewVector().scale(snapshot.resolvedVelocity()).add(0.0, 0.4, 0.0);
    }

    /// 新龙卷风已加入世界后才移除旧实体并写入新 UUID。
    @Override
    protected void onSuccessfulShot(ProjectileFireContext context, HurtnadoProjectile projectile) {
        ItemStack stack = context.currentWeaponForCommit();
        if (stack == null || stack.getItem() != this) {
            projectile.discard();
            return;
        }
        LibUtils.updateItemStackNbt(stack, tag -> {
            if (tag.hasUUID("UUID")
                    && context.level().getEntity(tag.getUUID("UUID")) instanceof HurtnadoProjectile old) {
                old.discard();
            }
            tag.putUUID("UUID", projectile.getUUID());
        });
    }
}
