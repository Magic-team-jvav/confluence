package org.confluence.mod.common.item.summon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachment.AttachmentEntityData;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.Minion;
import org.confluence.mod.common.summoner.minion.MinionSlotType;
import org.confluence.mod.util.PrefixUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SummonerWeaponItem<T extends Minion> extends Item {
    private final Supplier<AttachmentEntityType<T>> typeSupplier;
    private final MinionSlotType slotType;
    private final float damage;
    private final float knockback;
    private final float armorPierce;
    private final @Nullable Supplier<SoundEvent> soundEvent;
    private final TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonConsumer;
    private final TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeConsumer;

    public SummonerWeaponItem(Properties properties, Supplier<AttachmentEntityType<T>> typeSupplier, MinionSlotType slotType, float damage, float knockback, float armorPierce, @Nullable Supplier<SoundEvent> soundEvent, @Nullable TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonAction, @Nullable TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeAction) {
        super(properties);
        this.typeSupplier = typeSupplier;
        this.slotType = slotType;
        this.damage = damage;
        this.knockback = knockback;
        this.armorPierce = armorPierce;
        this.soundEvent = soundEvent;
        this.summonConsumer = summonAction != null ? summonAction : (weapon, player, itemStack) -> {
            T minion = weapon.createMinion(player, itemStack);
            SummonerHelper helper = SummonerHelper.get(player);
            MinionSlotType type = weapon.getSlotType(itemStack);
            if (helper.canSummon(type, minion.getSlotCost())) {
                AABB box = player.getBoundingBox();
                Vec3 pos = box.getCenter();
                minion.init(new PathNode(pos.offsetRandom(player.getRandom(), 2), 0, 0, 0));
                helper.add(minion);
            }
        };
        this.removeConsumer = removeAction != null ? removeAction : (weapon, player, itemStack) -> {
            AttachmentEntityData entityData = SummonerHelper.get(player).getEntityData();
            entityData.getGroups()
                    .getOrDefault(weapon.getEntityType(), List.of())
                    .stream()
                    .filter(entity -> entity instanceof Minion)
                    .forEach(AttachmentEntity::setRemove);
        };
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemCooldowns cooldowns = player.getCooldowns();
        if (!cooldowns.isOnCooldown(itemStack.getItem()) && !level.isClientSide()) {
            cooldowns.addCooldown(itemStack.getItem(), 4);
            player.swing(hand, true);
            if (player.isShiftKeyDown()) {
                remove(player, itemStack);
            } else {
                summon(player, itemStack);
            }
            SoundEvent sound = getSoundEvent(itemStack);
            if (sound != null) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), 1.0F, 1.0F);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @NotNull
    public AttachmentEntityType<T> getEntityType() {
        return typeSupplier.get();
    }

    @NotNull
    public MinionSlotType getSlotType(ItemStack itemStack) {
        return slotType;
    }

    public float getSummonDamage(@Nullable Player player, @NotNull ItemStack itemStack) {
        float result = damage;
        if (player != null) {
            AttributeInstance attribute = player.getAttribute(ConfluenceMagicLib.SUMMON_DAMAGE);
            if (attribute != null) {
                result *= (float) attribute.getValue();
            }
        }
        ModPrefix prefix = getPrefix(itemStack);
        if (prefix != null) {
            if (prefix instanceof ModPrefix.Summon summon) {
                result *= 1 + summon.attackDamage();
            }
            if (prefix instanceof ModPrefix.Universal universal) {
                result *= 1 + universal.attackDamage();
            }
        }
        return result;
    }

    public float getSummonKnockback(@Nullable Player player, @NotNull ItemStack itemStack) {
        float result = knockback;
        if (player != null) {
            AttributeInstance attribute = player.getAttribute(ConfluenceMagicLib.SUMMON_KNOCKBACK);
            if (attribute != null) {
                result += (float) attribute.getValue();
            }
        }
        ModPrefix prefix = getPrefix(itemStack);
        if (prefix != null) {
            if (prefix instanceof ModPrefix.Summon summon) {
                result *= 1 + summon.knockBack();
            }
            if (prefix instanceof ModPrefix.Universal universal) {
                result *= 1 + universal.knockBack();
            }
        }
        return result;
    }

    public float getSummonArmorPierce(@Nullable Player player, @NotNull ItemStack itemStack) {
        ModPrefix prefix = getPrefix(itemStack);
        if (prefix instanceof ModPrefix.Summon summon) {
            return armorPierce + summon.armorPenetration();
        }
        return armorPierce;
    }

    @Nullable
    public SoundEvent getSoundEvent(ItemStack itemStack) {
        return soundEvent == null ? null : soundEvent.get();
    }

    @NotNull
    public T createMinion(@NotNull Player player, @NotNull ItemStack itemStack) {
        T minion = getEntityType().factory().get();
        minion.setOwner(player);
        minion.setSlotType(getSlotType(itemStack));
        minion.setDamage(getSummonDamage(null, itemStack));
        minion.setKnockback(getSummonKnockback(null, itemStack));
        minion.setArmorPierce(getSummonArmorPierce(null, itemStack));
        PrefixComponent component = PrefixUtils.getPrefix(itemStack);
        if (component != null) {
            ModPrefix prefix = switch (component.type()) {
                case SUMMON -> ModPrefix.Summon.VALUES.get(component.name());
                case UNIVERSAL -> ModPrefix.Universal.VALUES.get(component.name());
                default -> null;
            };
            if (prefix != null) minion.setPrefix(prefix);
        }
        return minion;
    }

    public ModPrefix getPrefix(@NotNull ItemStack itemStack) {
        PrefixComponent component = PrefixUtils.getPrefix(itemStack);
        ModPrefix prefix = null;
        if (component != null) {
            prefix = switch (component.type()) {
                case SUMMON -> ModPrefix.Summon.VALUES.get(component.name());
                case UNIVERSAL -> ModPrefix.Universal.VALUES.get(component.name());
                default -> null;
            };
        }
        return prefix;
    }

    public void summon(@NotNull Player player, @NotNull ItemStack itemStack) {
        summonConsumer.accept(this, player, itemStack);
    }

    public void remove(@NotNull Player player, @NotNull ItemStack itemStack) {
        removeConsumer.accept(this, player, itemStack);
    }

    @NotNull
    public List<Component> getTooltips(ItemStack itemStack, Player player) {
        List<Component> tooltips = new ArrayList<>();
        if (damage > 0) {
            tooltips.add(Component.literal(String.format("%.1f ", getSummonDamage(player, itemStack))).withStyle(ChatFormatting.BLUE).append(Component.translatable("item.confluence.tooltip.damage").withStyle(ChatFormatting.GRAY)));
        }
        if (knockback > 0) {
            tooltips.add(Component.literal(String.format("%.1f ", getSummonKnockback(player, itemStack))).withStyle(ChatFormatting.BLUE).append(Component.translatable("item.confluence.tooltip.knockback").withStyle(ChatFormatting.GRAY)));
        }
        if (armorPierce > 0) {
            tooltips.add(Component.literal(String.format("%.1f ", getSummonArmorPierce(player, itemStack))).withStyle(ChatFormatting.BLUE).append(Component.translatable("item.confluence.tooltip.armor_pierce").withStyle(ChatFormatting.GRAY)));
        }
        tooltips.add(Component.translatable("item.confluence.tooltip.summon", typeSupplier.get().getDisplayName()).withStyle(ChatFormatting.GRAY));
        SummonerHelper helper = SummonerHelper.get(player);
        tooltips.add(Component.translatable(slotType == MinionSlotType.Sentry ? "item.confluence.tooltip.sentry_slots" : "item.confluence.tooltip.minion_slots", Component.literal(String.valueOf(helper.getUsedSlots(slotType))).withStyle(ChatFormatting.BLUE), Component.literal(String.valueOf(helper.getMaxCount(slotType))).withStyle(ChatFormatting.BLUE)).withStyle(ChatFormatting.GRAY));
        tooltips.add(Component.translatable("item.confluence.tooltip.remove_summon").withStyle(ChatFormatting.GRAY));
        return tooltips;
    }
}
