package org.confluence.mod.common.item.common;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EverBeneficialItem extends TooltipItem {
    public static final List<EverBeneficialItem> KNOWN_ITEMS = new ArrayList<>();

    private final ResourceLocation storageKey;
    private final ResourceLocation modifierId;
    private final int maxLevel;
    private final @Nullable Holder<Attribute> attribute;
    private final double amountPerLevel;
    private final AttributeModifier.Operation operation;
    private final Supplier<SoundEvent> useSound;
    private final BiConsumer<LivingEntity, Boolean> extraEffect;

    public EverBeneficialItem(
            ResourceLocation storageKey, int maxLevel,
            @Nullable Holder<Attribute> attribute, double amountPerLevel, AttributeModifier.Operation operation,
            Supplier<SoundEvent> useSound, BiConsumer<LivingEntity, Boolean> extraEffect,
            ModRarity rarity, List<Component> tooltips
    ) {
        super(new Properties().stacksTo(99), rarity, tooltips);
        this.storageKey = storageKey;
        this.modifierId = storageKey.withPrefix("beneficial/");
        this.maxLevel = maxLevel;
        this.attribute = attribute;
        this.amountPerLevel = amountPerLevel;
        this.operation = operation;
        this.useSound = useSound;
        this.extraEffect = extraEffect;
        KNOWN_ITEMS.add(this);
    }

    public static void refreshAll(LivingEntity entity) {
        if (!entity.isAlive()) return;
        EverBeneficial data = EverBeneficial.of(entity);
        for (EverBeneficialItem item : KNOWN_ITEMS) {
            item.apply(entity, data);
            item.extraEffect.accept(entity, true);
        }
    }

    public ResourceLocation getStorageKey() {
        return storageKey;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public BiConsumer<LivingEntity, Boolean> getExtraEffect() {
        return extraEffect;
    }

    public Supplier<SoundEvent> getUseSound() {
        return useSound;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        EverBeneficial data = EverBeneficial.of(player);

        if (level.isClientSide) {
            if (data.getLevel(this.storageKey) < this.maxLevel) {
                player.playSound(useSound.get(), 1.0F, 1.0F);
                return InteractionResultHolder.success(stack);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (data.tryIncrease(this.storageKey, this.maxLevel)) {
            apply(player, data);
            extraEffect.accept(player, false);
            if (!player.hasInfiniteMaterials()) stack.shrink(1);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    public void apply(LivingEntity entity, EverBeneficial data) {
        if (attribute == null) return;
        AttributeInstance inst = entity.getAttribute(attribute);
        if (inst != null) {
            inst.removeModifier(this.modifierId);
            int level = data.getLevel(storageKey);
            if (level > 0) {
                inst.addOrReplacePermanentModifier(new AttributeModifier(this.modifierId, amountPerLevel * level, operation));
            }
        }
    }
}
