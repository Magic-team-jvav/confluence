package org.confluence.mod.common.item.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.minecart.BaseMinecartEntity;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortItem;

@SuppressWarnings("rawtypes")
public class BaseMinecartItem extends MinecartItem {
    private final BaseMinecartEntity.Abilities abilities;
    private final MinecartFactory factory;

    public BaseMinecartItem(PortItem.PortProperties properties, ModRarity rarity, BaseMinecartEntity.Abilities abilities, MinecartFactory factory) {
        super(AbstractMinecart.Type.RIDEABLE, properties.stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.abilities = abilities;
        this.factory = factory;
    }

    public @Nullable AbstractMinecart createMinecart(ServerLevel level, double x, double y, double z, AbstractMinecart.Type type, ItemStack stack, @Nullable Player player) {
        if (type == AbstractMinecart.Type.RIDEABLE && stack.is(this)) {
            return factory.createMinecart(level, x, y, z, abilities);
        }
        return null;
    }

    @FunctionalInterface
    public interface MinecartFactory {
        AbstractMinecart createMinecart(ServerLevel level, double x, double y, double z, BaseMinecartEntity.Abilities abilities);
    }
}
