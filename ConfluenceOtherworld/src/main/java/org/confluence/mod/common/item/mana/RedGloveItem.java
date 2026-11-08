package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.mixed.Immunity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

/** Server-authoritative sustained mage weapon; visual assets are intentionally left for later. */
public final class RedGloveItem extends CustomRarityItem implements IWeaponTooltip, Immunity {
    public static final float BASE_DAMAGE = 92;
    public static final int MANA_COST = 18, IMMUNITY_TICKS = 4;
    /// 右键按住时的最大持续时间（约 1 小时，靠松手结束）
    private static final int MAX_USE_DURATION = 72000;
    public RedGloveItem() {
        super(new Properties().stacksTo(1), ModRarity.LIGHT_RED);
        addAttributeModifiers(builder -> builder.add(LibAttributes.getCriticalChance(),
                new AttributeModifier(Confluence.asResource("arc_surge"), 0.04, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND));
    }
    /// 右键按住持续放电；松开或切走由 {@link RedGloveData#tick()} 的 isUsingItem 校验收尾。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.getData(SummonerAttachmentTypes.RED_GLOVE_DATA).press(stack);
        }
        // 与原版弓/弩一致：在 use 内自行进入“使用中”，松手后由 releaseUsing / tick 校验结束
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return MAX_USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (living instanceof Player player && !level.isClientSide) {
            player.getData(SummonerAttachmentTypes.RED_GLOVE_DATA).reset();
        }
    }
    @Override public Holder<Attribute> getTooltipDamageAttribute() { return LibAttributes.getMagicDamage(); }
    @Override public double getTooltipDamage(ItemStack stack, double attributeValue) { return BASE_DAMAGE * attributeValue; }
    @Override public double getTooltipAttackSpeed(ItemStack stack, double attributeValue) { return 20.0 / IMMUNITY_TICKS; }
    @Override public int getEnchantmentValue(ItemStack stack) { return 20; }
    @Override public boolean isEnchantable(ItemStack stack) { return true; }
    @Override public Type confluence$getImmunityType() { return Type.STATIC; }
    @Override public int confluence$getImmunityDuration(DamageSource source) { return IMMUNITY_TICKS; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("tooltip.item.confluence.arc_surge.0").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("tooltip.confluence.mana_cost", MANA_COST).withStyle(ChatFormatting.GRAY));
    }
}
