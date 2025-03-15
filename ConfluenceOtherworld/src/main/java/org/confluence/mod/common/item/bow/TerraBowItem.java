package org.confluence.mod.common.item.bow;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.entity.projectile.BaseArrowEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terraentity.hit_effect.EffectStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class TerraBowItem extends BowItem {

    public float baseDamage;

    public Consumer<BaseArrowEntity.Builder> modifyArrowBuilder = b->{};

    public BaseArrowEntity.Builder arrowModifier;


    public TerraBowItem(float baseDamage, int durability, ModRarity rarity) {
        super(new Properties().durability(durability)
                .stacksTo(1)
                .component(TCDataComponentTypes.MOD_RARITY, rarity));
        this.baseDamage = baseDamage;
        this.arrowModifier = new BaseArrowEntity.Builder();
    }
    public TerraBowItem(float baseDamage, int durability, ModRarity rarity, Consumer<BaseArrowEntity.Builder> modifyArrowBuilder) {
        this(baseDamage, durability, rarity);
        this.modifyArrowBuilder = modifyArrowBuilder;
        modifyArrowBuilder.accept(this.arrowModifier);
    }

    // 无法破坏
    public TerraBowItem(float baseDamage, ModRarity rarity) {
        super(new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                .component(TCDataComponentTypes.MOD_RARITY, rarity).stacksTo(1));
        this.baseDamage = baseDamage;
        this.arrowModifier = new BaseArrowEntity.Builder();
    }
    public TerraBowItem(float baseDamage, ModRarity rarity, Consumer<BaseArrowEntity.Builder> modifyArrowBuilder) {
        this(baseDamage, rarity);
        this.modifyArrowBuilder = modifyArrowBuilder;
        modifyArrowBuilder.accept(this.arrowModifier);
    }

    @Override
    public @NotNull AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        arrow.setBaseDamage(baseDamage);
        return arrow;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        super.releaseUsing(stack, level, entity, timeLeft);
        if(stack.is(ModTags.Items.FAST_BOW) && entity instanceof Player p){
            p.getCooldowns().addCooldown(this, 5);
            ItemStack off = p.getOffhandItem();
            if(off.getItem() instanceof BowItem){
                p.getCooldowns().addCooldown(off.getItem(), 5);
            }
        }

    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if(!this.arrowModifier.fullPullHitEffect.isEmpty()) {
            float f = getUseDuration(stack, entity) - remainingUseDuration;
            if (f < 16)
                entity.getData(ModAttachmentTypes.WEAPON_STORAGE).bowFullPull = false;
            else if (f == 16) {
                entity.getData(ModAttachmentTypes.WEAPON_STORAGE).bowFullPull = true;
                if (level.isClientSide)
                    entity.playSound(ModSoundEvents.COOLDOWN_RECOVERY.get());

            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("attribute.name.generic.attack_damage").append(": ").append(String.format("%.1f", this.baseDamage)).withColor(0x00FF00));

        if(!this.arrowModifier.onHitEffect.isEmpty()){
            EffectStrategy.appendDescription(tooltipComponents,
                    this.arrowModifier.onHitEffect.stream().map(DeferredHolder::get).toList(),
                    Component.translatable("tooltip.item.confluence.on_hit_effects").append(": ").withColor(0xFF00FF));
        }
        if(!this.arrowModifier.fullPullHitEffect.isEmpty()){
            EffectStrategy.appendDescription(tooltipComponents,
                    this.arrowModifier.fullPullHitEffect.stream().map(DeferredHolder::get).toList(),
                    Component.translatable("tooltip.item.confluence.bow_full_pull_on_hit_effects").append(": ").withColor(0xFF00FF));
        }

        Item transformArrow = arrowModifier.getTransformArrow();
        if(transformArrow!=null){
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.arrow_transform").append(": ").append(Component.translatable(transformArrow.getDescriptionId())).withColor(0xF1b0F4));
        }

    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static float getFastBowPowerForTime(int pCharge) {
        float f = pCharge / 20.0f;
        f = (f * f + f * 2.0F) / 3 * 0.5f + 0.5f; // 0.5f< f < 0.7+0.5
        f = Math.min(f, 1.5F);
        return f;
    }
}
