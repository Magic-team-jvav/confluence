package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.ThunderstrikeProjectile;
import org.confluence.mod.Confluence;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

/** The server resolves the aim from its own player rotation and bounded scroll distance. */
public final class ThunderstrikeItem extends ManaStaffItem<ThunderstrikeProjectile> {
    public static final String RANGE_KEY = "confluence.thunderstrike_range";
    public static final double DEFAULT_RANGE = 16;

    public ThunderstrikeItem() {
        super(ModRarity.PINK, ThunderstrikeProjectile::new, 45, 12, 8, 20, 0.14);
        withTooltip(net.minecraft.network.chat.Component.translatable("tooltip.item.confluence.thunderstrike.0")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        addAttributeModifiers(builder -> builder.add(Attributes.ATTACK_KNOCKBACK,
                new PortAttributeModifier(Confluence.asResource("thunderstrike"), 0.5, PortAttributeModifier.Operation.ADD_VALUE),
                PortEquipmentSlotGroup.MAINHAND));
    }

    public static boolean held(Player player) {
        return player.getMainHandItem().getItem() instanceof ThunderstrikeItem
                || player.getOffhandItem().getItem() instanceof ThunderstrikeItem;
    }

    public static double maxRange(Player player) {
        // Attribute base is excluded: only reach bonuses extend the sixteen-block range.
        var reach = player.getAttribute(ForgeMod.BLOCK_REACH.get());
        return DEFAULT_RANGE + (reach == null ? 0 : Math.max(0, reach.getValue() - reach.getAttribute().getDefaultValue()));
    }

    public static double range(Player player) {
        double value = player.getPersistentData().contains(RANGE_KEY)
                ? player.getPersistentData().getDouble(RANGE_KEY) : DEFAULT_RANGE;
        return Mth.clamp(Double.isFinite(value) ? value : DEFAULT_RANGE, 1, maxRange(player));
    }

    public static Vec3 target(Player player) {
        Vec3 eye = player.getEyePosition();
        return player.level().clip(new ClipContext(eye, eye.add(player.getLookAngle().scale(range(player))),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getLocation();
    }

    @Override protected void beforeShoot(ServerPlayer player, ItemStack stack, ThunderstrikeProjectile projectile) {
        super.beforeShoot(player, stack, projectile);
        projectile.aimAt(target(player));
    }

    @Override protected void rayTrace(ServerPlayer player, ItemStack stack, ThunderstrikeProjectile projectile) {
        // The spark performs swept collision over its actual ten-tick journey.
    }
}
