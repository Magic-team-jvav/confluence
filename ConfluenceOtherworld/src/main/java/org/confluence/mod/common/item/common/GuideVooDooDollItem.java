package org.confluence.mod.common.item.common;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;

public class GuideVooDooDollItem extends BaseCurioItem {
    public GuideVooDooDollItem(String name) {
        super(builder(name).rarity(ModRarity.WHITE));
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        if (damageSource.is(DamageTypes.LAVA)) {
            CompoundTag tag = LibUtils.getItemStackNbtIfPresent(itemEntity.getItem());
            if (tag == null) {
                // 按绝对坐标决定
            } else {
                // 按玩家朝向决定
            }
        }
    }

    public static void setDirection(ItemStack itemStack, Player owner) {
        LibUtils.updateItemStackNbt(itemStack, tag -> tag.put("Direction", Direction.CODEC.encodeStart(NbtOps.INSTANCE, owner.getDirection()).getOrThrow()));
    }
}
