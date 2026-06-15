package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.HurtnadoProjectile;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.Optional;

public class WeatherPainItem extends ManaStaffItem<HurtnadoProjectile> {
    private TooltipComponent component;

    public WeatherPainItem() {
        super(ModRarity.GREEN, HurtnadoProjectile::new, 6.5F, 30, 1.0F, 45, builder -> builder
                .add(LibAttributes.getCriticalChance(), new PortAttributeModifier(ID, 0.04, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND)
                .add(LibAttributes.getArmorPenetration(), new PortAttributeModifier(ID, 10, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(component);
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack stack, HurtnadoProjectile projectile) {
        super.beforeShoot(player, stack, projectile);
        projectile.addDeltaMovement(new Vec3(0.0, 0.4, 0.0));
    }

    @Override
    protected void afterShoot(ServerPlayer player, ItemStack stack, HurtnadoProjectile projectile) {
        super.afterShoot(player, stack, projectile);
        LibUtils.updateItemStackNbt(stack, tag -> {
            if (tag.hasUUID("UUID") && player.serverLevel().getEntity(tag.getUUID("UUID")) instanceof HurtnadoProjectile projectile1) {
                projectile1.discard();
            }
            tag.putUUID("UUID", projectile.getUUID());
        });
    }
}
