package org.confluence.mod.common.item.bow;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.BaseArrowEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TerraBowItem extends BowItem {

    public Consumer<BaseArrowEntity.Builder> modifyArrowBuilder;
    public float baseDamage;

    public TerraBowItem(float baseDamage, int durability, ModRarity rarity) {
        super(new Properties().durability(durability)
                .component(TCDataComponentTypes.MOD_RARITY, rarity));
        this.baseDamage = baseDamage;
    }
    public TerraBowItem(float baseDamage, int durability, ModRarity rarity, Consumer<BaseArrowEntity.Builder> modifyArrowBuilder) {
        this(baseDamage, durability, rarity);
        this.modifyArrowBuilder = modifyArrowBuilder;
        this.baseDamage = baseDamage;
    }

    // 无法破坏
    public TerraBowItem(float baseDamage, ModRarity rarity) {
        super(new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                .component(TCDataComponentTypes.MOD_RARITY, rarity));
        this.baseDamage = baseDamage;
    }
    public TerraBowItem(float baseDamage, ModRarity rarity, Consumer<BaseArrowEntity.Builder> modifyArrowBuilder) {
        this(baseDamage, rarity);
        this.modifyArrowBuilder = modifyArrowBuilder;
        this.baseDamage = baseDamage;
    }

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack pStack) {
        return Component.translatable(getDescriptionId()).withStyle(style -> style.withColor(pStack.get(TCDataComponentTypes.MOD_RARITY).getColor()));
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
        }

    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if(stack.is(ModTags.Items.FAST_BOW)) {
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


    public static float getFastBowPowerForTime(int pCharge) {
        float f = pCharge / 20.0f;
        f = (f * f + f * 2.0F) / 3.0F + 0.5f; // 0.5f< f < 3/3+0.5
        f = Math.min(f, 1.5F);
        return f;
    }
}
