package org.confluence.mod.integration.sodium.iris;

import net.irisshaders.iris.api.v0.item.IrisItemLightProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class TestItem extends Item implements IrisItemLightProvider {
    private static final Vector3f color = new Vector3f(1, 0, 0);

    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getLightEmission(Player player, ItemStack stack) {
        return 15;
    }

    @Override
    public Vector3f getLightColor(Player player, ItemStack stack) {
        return color;
    }
}
