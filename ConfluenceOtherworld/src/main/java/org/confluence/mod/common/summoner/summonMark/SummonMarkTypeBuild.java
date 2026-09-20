package org.confluence.mod.common.summoner.summonMark;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class SummonMarkTypeBuild {
    private final ResourceLocation identifier;
    private float additionalDamage;
    private float additionalArmorPierce;
    private float criticalHitRate;
    private @Nullable BiConsumer<LivingEntity, Player> tickConsumer;
    private SummonMarkType.@Nullable SummonMarkDamagePreConsumer damagePre;
    private SummonMarkType.@Nullable SummonMarkDamagePostConsumer damagePost;
    private @Nullable BiConsumer<LivingEntity, AttachmentEntityDamageSource> killConsumer;

    public SummonMarkTypeBuild(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public SummonMarkTypeBuild damage(float damage) {
        this.additionalDamage = Math.max(0, damage);
        return this;
    }

    public SummonMarkTypeBuild armorPierce(float armorPierce) {
        this.additionalArmorPierce = Math.max(0, armorPierce);
        return this;
    }

    public SummonMarkTypeBuild criticalRate(float criticalRate) {
        this.criticalHitRate = Math.max(0, Math.min(1, criticalRate));
        return this;
    }

    public SummonMarkTypeBuild onTick(BiConsumer<LivingEntity, Player> tickConsumer) {
        this.tickConsumer = tickConsumer;
        return this;
    }

    public SummonMarkTypeBuild onDamagePre(SummonMarkType.SummonMarkDamagePreConsumer consumer) {
        this.damagePre = consumer;
        return this;
    }

    public SummonMarkTypeBuild onDamagePost(SummonMarkType.SummonMarkDamagePostConsumer consumer) {
        this.damagePost = consumer;
        return this;
    }

    public SummonMarkTypeBuild onKill(BiConsumer<LivingEntity, AttachmentEntityDamageSource> killConsumer) {
        this.killConsumer = killConsumer;
        return this;
    }

    public SummonMarkType build() {
        return new SummonMarkType(identifier, additionalDamage, additionalArmorPierce, criticalHitRate, tickConsumer, damagePre, damagePost, killConsumer);
    }
}
