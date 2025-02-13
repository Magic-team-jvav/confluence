package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.VilethronProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCAttributes;

public class VilethronItem extends CustomRarityItem {
    public VilethronItem() {
        super(new Properties().stacksTo(1), ModRarity.BLUE);
        addAttributeModifiers(builder -> builder.add(TCAttributes.getCriticalChance(), new AttributeModifier(Confluence.asResource("vilethron"), 0.04, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer && PlayerUtils.extractMana(serverPlayer, () -> PrefixUtils.calculateManaCost(itemStack, 10))) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            VilethronProjectile vilethron = new VilethronProjectile(player);
            vilethron.shootFromRotation(serverPlayer, serverPlayer.getXRot(), serverPlayer.getYRot(), 0.0F, 1.0F, 0.0F);
            level.addFreshEntity(vilethron);
            player.getCooldowns().addCooldown(this, 28);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.REGULAR_STAFF_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
