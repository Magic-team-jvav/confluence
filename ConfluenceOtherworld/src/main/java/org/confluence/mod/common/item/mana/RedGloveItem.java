package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.item.ILeftClickStateItem;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.List;

/** Server-authoritative sustained mage weapon; visual assets are intentionally left for later. */
public final class RedGloveItem extends CustomRarityItem implements ILeftClickStateItem, IWeaponTooltip, Immunity {
    public static final float BASE_DAMAGE = 92;
    public static final int MANA_COST = 18;
    public RedGloveItem() {
        super(new Properties().stacksTo(1), ModRarity.LIGHT_RED);
        addAttributeModifiers(builder -> builder.add(LibAttributes.getCriticalChance(),
                new PortAttributeModifier(Confluence.asResource("arc_surge"), 0.04, PortAttributeModifier.Operation.ADD_VALUE),
                PortEquipmentSlotGroup.MAINHAND));
    }
    @Override public void onLeftClick(Player player, ItemStack stack) {
        player.getData(SummonerAttachmentTypes.RED_GLOVE_DATA).press(stack);
    }
    @Override public void onLeftRelease(Player player, ItemStack stack) {
        player.getData(SummonerAttachmentTypes.RED_GLOVE_DATA).reset();
    }
    @Override public Attribute getTooltipDamageAttribute() { return LibAttributes.getMagicDamage().value(); }
    @Override public double getTooltipDamage(ItemStack stack, double attributeValue) { return BASE_DAMAGE * attributeValue; }
    @Override public double getTooltipAttackSpeed(ItemStack stack, double attributeValue) { return 20.0 / RedGlovePattern.CAST_TICKS; }
    @Override public int getEnchantmentValue(ItemStack stack) { return 20; }
    @Override public boolean isEnchantable(ItemStack stack) { return true; }
    @Override public Type confluence$getImmunityType() { return Type.STATIC; }
    /// 不写入局部无敌帧：仍是 Immunity 实现（借用其清零原版受伤帧的结算），但每次放电都独立造成伤害。
    @Override public int confluence$getImmunityDuration(DamageSource source) { return 0; }
    @Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        lines.add(Component.translatable("tooltip.item.confluence.arc_surge.0").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("tooltip.confluence.mana_cost", MANA_COST).withStyle(ChatFormatting.GRAY));
    }
}
