package org.confluence.terra_curio.common.item.curio.combat;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.attachment.AccessoriesAttachment;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terra_curio.util.CuriosUtils;
import top.theillusivec4.curios.api.SlotContext;

public class PanicNecklace extends BaseCurioItem {
    public static final String KEY = TerraCurio.MODID + ":last_hurt";

    public PanicNecklace(Builder builder) {
        super(builder);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
        CompoundTag nbt = LibUtils.getOrCreatePersistedData(player);
        long lastHurt = nbt.getLong(KEY);
        if (lastHurt == 0) return;
        if (player.level().getGameTime() - lastHurt > 160) {
            nbt.putLong(KEY, 0);
        }
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return EMPTY_ATTRIBUTE;
        return LibUtils.getOrCreatePersistedData(player).getLong(KEY) == 0 ? EMPTY_ATTRIBUTE : super.getAttributeModifiers(slotContext, id, stack);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return CuriosUtils.noSameCurio(entity, PanicNecklace.class);
    }

    public static void apply(LivingEntity living) {
        if (living instanceof Player player && AccessoriesAttachment.of(player).hasPanicNecklace()) {
            LibUtils.getOrCreatePersistedData(player).putLong(KEY, player.level().getGameTime());
        }
    }
}
