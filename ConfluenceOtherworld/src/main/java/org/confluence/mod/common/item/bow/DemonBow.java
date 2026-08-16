package org.confluence.mod.common.item.bow;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.projectile.*;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.List;

import static org.confluence.lib.common.component.ModRarity.BLUE;

/// 满蓄力命中后通过父箭冻结快照生成固定七点基础伤害的腐化剑气。
public class DemonBow extends BaseTerraBowItem {
    public DemonBow() {
        super(4.9F, new Properties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new BaseArrowEntity(ModEntities.BASE_ARROW.get(), shooter, ammo, weapon) {
            @Override
            protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
                if (!fullPull || !(owner instanceof ServerPlayer player)) return;
                ProjectileCombatSnapshot parentSnapshot =
                        ((ProjectileCombatSnapshotCarrier) this).getProjectileCombatSnapshot();
                if (parentSnapshot == null) {
                    ConfluenceMagicLib.LOGGER.error(
                            "Demon bow full-pull arrow hit without a projectile combat snapshot");
                    return;
                }
                var projectile = ModEntities.LIGHTS_BANE.get().create(owner.level());
                if (projectile == null) {
                    ConfluenceMagicLib.LOGGER.error(
                            "Failed to create demon bow derived projectile");
                    return;
                }
                projectile.addAttackDamage(7.0F);
                projectile.setOwner(owner);
                Vec3 position = target.position().add(
                        target.getRandom().nextFloat() * 0.2F,
                        target.getEyeHeight() * 0.5F,
                        target.getRandom().nextFloat() * 0.2F);
                ProjectileCombatSnapshot derivedSnapshot = parentSnapshot.derive(7.0F, 1.0F, 0.0F);
                ProjectileFireResult result = ServerProjectileFireService.spawnDerived(
                        player,
                        derivedSnapshot,
                        List.of(new ProjectileLaunch(
                                projectile, position, new Vec3(0.0, 1.0, 0.0), 0.0F)));
                if (result != ProjectileFireResult.SUCCESS) {
                    ConfluenceMagicLib.LOGGER.error(
                            "Demon bow derived projectile was rejected: {}", result);
                }
            }
        };
    }
}
