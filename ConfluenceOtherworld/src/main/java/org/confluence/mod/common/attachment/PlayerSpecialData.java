package org.confluence.mod.common.attachment;

import com.google.common.collect.Iterables;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModArmorBonus;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.attachment.PrimitiveValueHolder;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class PlayerSpecialData extends PrimitiveValueHolder {
    public static final Set<ValueType<Unit, UnitValue>> UNITS_REQUIRE_UPDATE = Util.make(new LinkedHashSet<>(), list -> {

    });
    public static final Set<ValueType<?, ? extends PrimitiveValue<?>>> OTHER_REQUIRE_UPDATE = Util.make(new LinkedHashSet<>(), list -> {
        list.add(TCItems.LUMINANCE);
    });
    private @NotNull ModArmorBonus.Key armorSetBonusKey = ModArmorBonus.Key.NONE;
    private transient PrimitiveValueComponent armorSetBonus;
    private ItemStack currentQuestedFish;
    private ITradeLock currentQuestedFishCondition;

    private boolean couldHurtCritters;
    private boolean couldDamageEnvironment;

    @Override
    public void setToDefaultValue() {
        super.setToDefaultValue();
        this.currentQuestedFish = ItemStack.EMPTY;
        this.currentQuestedFishCondition = ITradeLock.alwaysTrue();
        this.couldHurtCritters = true;
        this.couldDamageEnvironment = true;
    }

    public void setCurrentQuestedFish(ItemStack cost, ITradeLock lock) {
        this.currentQuestedFish = cost;
        this.currentQuestedFishCondition = lock;
    }

    public void removeCurrentQuestedFish() {
        setCurrentQuestedFish(ItemStack.EMPTY, ITradeLock.alwaysTrue());
    }

    public ItemStack getCurrentQuestedFish(Player player) {
        if (currentQuestedFishCondition.canTrade(player, ITradeHolder.dummy(player), 0)) {
            return currentQuestedFish;
        }
        return ItemStack.EMPTY;
    }

    public void setCouldHurtCritters(boolean couldHurtCritters) {
        this.couldHurtCritters = couldHurtCritters;
    }

    public boolean isCouldHurtCritters() {
        return couldHurtCritters;
    }

    public void setCouldDamageEnvironment(boolean couldDamageEnvironment) {
        this.couldDamageEnvironment = couldDamageEnvironment;
    }

    public boolean isCouldDamageEnvironment() {
        return couldDamageEnvironment;
    }

    @Override
    protected void flushAbility(LivingEntity living) {
        super.flushAbility(living);

        if (!ModArmorBonus.shouldOverrideSingleBonus(armorSetBonusKey)) {
            for (ItemStack stack : living.getArmorSlots()) {
                PrimitiveValueComponent component = ModArmorBonus.getSingleArmorBonus(stack);
                if (component != null) compute(component);
            }
        }
        if (armorSetBonus == null) this.armorSetBonus = ModArmorBonus.getArmorSetBonus(armorSetBonusKey);
        if (armorSetBonus != null) compute(armorSetBonus);
    }

    /**
     * @see LivingEntity#collectEquipmentChanges()
     */
    public void flushArmorSetBonus(Player player) {
        Inventory inventory = player.getInventory();
        ItemStack head = inventory.getArmor(EquipmentSlot.HEAD.getIndex());
        ItemStack chest = inventory.getArmor(EquipmentSlot.CHEST.getIndex());
        ItemStack legs = inventory.getArmor(EquipmentSlot.LEGS.getIndex());
        ItemStack feet = inventory.getArmor(EquipmentSlot.FEET.getIndex());
        ModArmorBonus.Key key = ModArmorBonus.Key.of(head, chest, legs, feet);
        if (armorSetBonusKey.equals(key)) return;
        AttributeMap attributes = player.getAttributes();

        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : getValue(TCItems.ATTRIBUTES).asMap().entrySet()) {
            AttributeInstance attributeinstance = attributes.getInstance(entry.getKey());
            if (attributeinstance == null) continue;
            for (AttributeModifier modifier : entry.getValue()) {
                attributeinstance.removeModifier(modifier);
            }
        }

        PrimitiveValueComponent component = ModArmorBonus.getArmorSetBonus(key);
        if (component == null) {
            this.armorSetBonusKey = ModArmorBonus.Key.NONE;
            this.armorSetBonus = null;
        } else {
            this.armorSetBonusKey = key;
            this.armorSetBonus = component;
        }
        flushAbility(player);

        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : getValue(TCItems.ATTRIBUTES).asMap().entrySet()) {
            AttributeInstance attributeinstance = attributes.getInstance(entry.getKey());
            if (attributeinstance == null) continue;
            for (AttributeModifier modifier : entry.getValue()) {
                attributeinstance.removeModifier(modifier);
                attributeinstance.addTransientModifier(modifier);
            }
        }
    }

    @Override
    protected Set<ValueType<Unit, UnitValue>> getUnitsRequireUpdate() {
        return UNITS_REQUIRE_UPDATE;
    }

    @Override
    protected Set<ValueType<?, ? extends PrimitiveValue<?>>> getOtherRequireUpdate() {
        return OTHER_REQUIRE_UPDATE;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        CompoundTag tag = super.serializeNBT(provider);

        ModArmorBonus.Key.CODEC.encodeStart(ops, armorSetBonusKey).ifSuccess(nbt -> tag.put("ArmorBonusKey", nbt));
        tag.put("CurrentQuestedFish", ItemStack.OPTIONAL_CODEC.encodeStart(ops, currentQuestedFish).result().orElseGet(CompoundTag::new));
        tag.put("CurrentQuestedFishCondition", ITradeLock.TYPED_CODEC.encodeStart(ops, currentQuestedFishCondition).result().orElseGet(CompoundTag::new));
        tag.putBoolean("CouldHurtCritters", couldHurtCritters);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        super.deserializeNBT(provider, nbt);

        if (nbt.contains("ArmorBonusKey")) this.armorSetBonusKey = ModArmorBonus.Key.CODEC.parse(ops, nbt.get("ArmorBonusKey")).result().orElse(ModArmorBonus.Key.NONE);
        this.currentQuestedFish = ItemStack.OPTIONAL_CODEC.parse(ops, nbt.get("CurrentQuestedFish")).result().orElse(ItemStack.EMPTY);
        this.currentQuestedFishCondition = ITradeLock.TYPED_CODEC.parse(ops, nbt.get("CurrentQuestedFishCondition")).result().orElse(ITradeLock.alwaysTrue());
        this.couldHurtCritters = nbt.getBoolean("CouldHurtCritters");
    }

    public static PlayerSpecialData of(Player player) {
        return player.getData(ModAttachmentTypes.SPECIAL_DATA);
    }

    public static void resetSomeData(Player player) {
        PlayerSpecialData data = of(player);
        if (!data.isCouldHurtCritters() || !data.isCouldDamageEnvironment()) {
            data.setCouldHurtCritters(true);
            data.setCouldDamageEnvironment(true);
            for (ItemStack stack : Iterables.concat(player.getInventory().offhand, player.getInventory().items)) {
                boolean a = data.isCouldHurtCritters();
                boolean b = data.isCouldDamageEnvironment();
                if (!a && !b) break;
                Item item = stack.getItem();
                boolean c = item == ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.get();
                if (a && (c || item == ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.get())) {
                    data.setCouldHurtCritters(false);
                }
                if (b && (c || item == ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.get())) {
                    data.setCouldDamageEnvironment(false);
                }
            }
        }
    }
}
