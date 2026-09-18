package org.confluence.mod.common.summoner.summonMark;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.summoner.attachment.WhipTracker;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public record SummonMarkType(
        ResourceLocation identifier,
        float additionalDamage,
        float additionalArmorPierce,
        float criticalHitRate,
        @Nullable BiConsumer<LivingEntity, Player> tickConsumer,
        @Nullable SummonMarkDamageConsumer damagePre,
        @Nullable SummonMarkDamageConsumer damagePost,
        @Nullable BiConsumer<LivingEntity, AttachmentEntityDamageSource> killConsumer
) {
    public List<Component> getTooltips() {
        List<Component> tooltips = new ArrayList<>();
        if (additionalDamage > 0) {
            tooltips.add(Component.literal(String.format("%.1f ", additionalDamage))
                    .withStyle(ChatFormatting.BLUE)
                    .append(Component.translatable("mark.additional_damage").withStyle(ChatFormatting.GRAY)));
        }
        if (additionalArmorPierce > 0) {
            tooltips.add(Component.literal(String.format("%.1f ", additionalArmorPierce))
                    .withStyle(ChatFormatting.BLUE)
                    .append(Component.translatable("mark.additional_armor_pierce").withStyle(ChatFormatting.GRAY)));
        }
        if (criticalHitRate > 0) {
            tooltips.add(Component.literal(String.format("%.0f%% ", criticalHitRate * 100))
                    .withStyle(ChatFormatting.BLUE)
                    .append(Component.translatable("mark.critical_hit_rate").withStyle(ChatFormatting.GRAY)));
        }
        return tooltips;
    }

    public void tick(LivingEntity target, Player owner) {
        if (tickConsumer != null) {
            tickConsumer.accept(target, owner);
        }
    }

    public float damagePre(WhipTracker tracker, LivingEntity target, AttachmentEntityDamageSource source, float damage) {
        if (damagePre != null) {
            return damagePre.accept(tracker, target, source, damage);
        }
        return damage;
    }

    public void damagePost(WhipTracker tracker, LivingEntity target, AttachmentEntityDamageSource source, float damage) {
        if (damagePost != null) {
            damagePost.accept(tracker, target, source, damage);
        }
    }

    public void kill(LivingEntity target, AttachmentEntityDamageSource source) {
        if (killConsumer != null) {
            killConsumer.accept(target, source);
        }
    }

    @FunctionalInterface
    public interface SummonMarkDamageConsumer {
        float accept(WhipTracker tracker, LivingEntity target, AttachmentEntityDamageSource source, float damage);
    }
}
