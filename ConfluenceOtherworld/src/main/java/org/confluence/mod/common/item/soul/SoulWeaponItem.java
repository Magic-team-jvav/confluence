package org.confluence.mod.common.item.soul;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.util.PlayerUtils;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulWeaponItem extends CustomRarityItem implements IWeaponTooltip {
    protected float damage;

    public SoulWeaponItem(Properties properties, ModRarity rarity, float damage) {
        super(properties, rarity);
        this.damage = damage;
    }

    public float getSoulDamage(ItemStack stack) {
        return damage;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (!PlayerUtils.castSoulSkill(serverPlayer, usedHand)) {
                return InteractionResultHolder.fail(stack);
            }
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public Holder<Attribute> getTooltipDamageAttribute() {
        return LibAttributes.getSoulDamage();
    }

    @Override
    public double getTooltipDamage(ItemStack stack, double attributeValue) {
        return getSoulDamage(stack) * attributeValue;
    }
}
