package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.HurtnadoProjectile;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;

import java.util.Optional;

public class WeatherPainItem extends ManaStaffItem<HurtnadoProjectile> {
    private TooltipComponent component;

    public WeatherPainItem() {
        super(ModRarity.GREEN, HurtnadoProjectile::new, 6.5F, 30, 1.0F, 45, builder -> builder
                .add(LibAttributes.getCriticalChance(), new AttributeModifier(ID, 0.04, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(LibAttributes.getArmorPenetration(), new AttributeModifier(ID, 10, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(component);
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack itemStack, HurtnadoProjectile projectile) {
        super.beforeShoot(player, itemStack, projectile);
        projectile.addDeltaMovement(new Vec3(0.0, 0.4, 0.0));
    }

    @Override
    protected void afterShoot(ServerPlayer player, ItemStack itemStack, HurtnadoProjectile projectile) {
        super.afterShoot(player, itemStack, projectile);
        LibUtils.updateItemStackNbt(itemStack, tag -> {
            if (tag.hasUUID("UUID") && player.serverLevel().getEntity(tag.getUUID("UUID")) instanceof HurtnadoProjectile projectile1) {
                projectile1.discard();
            }
            tag.putUUID("UUID", projectile.getUUID());
        });
    }
}
