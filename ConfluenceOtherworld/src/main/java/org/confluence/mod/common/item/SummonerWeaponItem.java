package org.confluence.mod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachment.AttachmentEntityData;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.Minion;
import org.confluence.mod.common.summoner.minion.MinionSlotType;
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
    private final @Nullable SoundEvent soundEvent;
    private final TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonConsumer;
    private final TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeConsumer;

    public SummonerWeaponItem(Properties properties, Supplier<AttachmentEntityType<T>> typeSupplier) {
        this(properties, typeSupplier, MinionSlotType.Minion, 0, 0, null, null, null);
    }

    public SummonerWeaponItem(Properties properties, Supplier<AttachmentEntityType<T>> typeSupplier,
                              @Nullable TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonAction,
                              @Nullable TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeAction) {
        this(properties, typeSupplier, MinionSlotType.Minion, 0, 0, null, summonAction, removeAction);
    }

    public SummonerWeaponItem(Properties properties, Supplier<AttachmentEntityType<T>> typeSupplier,
                              MinionSlotType slotType, float damage, float knockback,
                              @Nullable SoundEvent soundEvent,
                              @Nullable TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonAction,
                              @Nullable TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeAction) {
        super(properties);
        this.typeSupplier = typeSupplier;
        this.slotType = slotType;
        this.damage = damage;
        this.knockback = knockback;
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
            if (soundEvent != null) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, player.getSoundSource(), 1.0F, 1.0F);
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
        return result;
    }

    @Nullable
    public SoundEvent getSoundEvent(ItemStack itemStack) {
        return soundEvent;
    }

    @NotNull
    public T createMinion(@NotNull Player player, @NotNull ItemStack itemStack) {
        T minion = getEntityType().factory().get();
        minion.setOwner(player);
        minion.setSlotType(getSlotType(itemStack));
        minion.setDamage(getSummonDamage(player, itemStack));
        minion.setKnockback(getSummonKnockback(player, itemStack));
        return minion;
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
        ResourceLocation location = getEntityType().location();
        if (damage > 0) {
            tooltips.add(Component.literal(String.format("%.1f ", getSummonDamage(player, itemStack)))
                    .withStyle(ChatFormatting.BLUE)
                    .append(Component.translatable("item.confluence.tooltip.damage").withStyle(ChatFormatting.GRAY)));
        }
        if (knockback > 0) {
            tooltips.add(Component.literal(String.format("%.1f ", getSummonKnockback(player, itemStack)))
                    .withStyle(ChatFormatting.BLUE)
                    .append(Component.translatable("item.confluence.tooltip.knockback").withStyle(ChatFormatting.GRAY)));
        }
        tooltips.add(Component.translatable("item.confluence.tooltip.summon",
                Component.translatable("summon." + location.getNamespace() + "." + location.getPath())).withStyle(ChatFormatting.GRAY));

        SummonerHelper helper = SummonerHelper.get(player);
        int used = helper.getUsedSlots(slotType);
        int max = helper.getMaxCount(slotType);
        tooltips.add(Component.translatable("item.confluence.tooltip.summon_slots",
                Component.literal(String.valueOf(used)).withStyle(ChatFormatting.BLUE),
                Component.literal(String.valueOf(max)).withStyle(ChatFormatting.BLUE)).withStyle(ChatFormatting.GRAY));
        tooltips.add(Component.translatable("item.confluence.tooltip.remove_summon").withStyle(ChatFormatting.GRAY));
        return tooltips;
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A first, B second, C third);
    }
}
