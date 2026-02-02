package org.confluence.terra_curio.common.attachment;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.api.event.AfterAccessoryAbilitiesFlushedEvent;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUpdateEvent;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCAttachments;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_curio.common.item.curio.combat.PanicNecklace;
import org.confluence.terra_curio.util.TCUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.LinkedHashSet;
import java.util.Set;

@javax.annotation.ParametersAreNonnullByDefault
@net.minecraft.MethodsReturnNonnullByDefault
public class AccessoriesAttachment extends PrimitiveValueHolder {
    public static final Set<ValueType<Unit, UnitValue>> UNITS_REQUIRE_UPDATE = Util.make(new LinkedHashSet<>(), list -> {
        list.add(TCItems.FLOAT$ON$LIQUID$SURFACE);
        list.add(TCItems.FIRE$ATTACK);
        list.add(TCItems.BRAIN$OF$CONFUSION);
        list.add(TCItems.HIVE$PACK);
        list.add(TCItems.HONEY$COMB);
        list.add(TCItems.MAGIC$QUIVER);
        list.add(TCItems.IGNITE$ARROW);
        list.add(TCItems.FROZEN$TURTLE$SHELL);
        list.add(TCItems.FIRE$IMMUNE);
        list.add(TCItems.FLOWER$BOOTS);
        list.add(TCItems.FROZEN$IMMUNE);
        list.add(TCItems.ICE$SPEED);
        list.add(TCItems.DIVING);
        list.add(TCItems.INFINITE$FLIGHT);
        list.add(TCItems.ICE$SAFE);
        list.add(TCItems.SHIELD$OF$CTHULHU);
        ModLoader.postEvent(new RegisterAccessoriesComponentUpdateEvent.UnitType(list));
    });
    public static final Set<ValueType<?, ? extends PrimitiveValue<?>>> OTHER_REQUIRE_UPDATE = Util.make(new LinkedHashSet<>(), list -> {
        list.add(TCItems.NEPTUNES$SHELL);
        list.add(TCItems.STAR$CLOCK);
        list.add(TCItems.INJURY$FREE);
        list.add(TCItems.INVULNERABLE$TICKS$MULTIPLIER);
        list.add(TCItems.LAVA$HURT$REDUCE);
        list.add(TCItems.LAVA$IMMUNE$TICKS);
        list.add(TCItems.RIGHT$CLICK$DELAY$SUBSTRACTOR);
        list.add(TCItems.MOB$IGNORE);
        list.add(TCItems.WALL$CLIMB);
        list.add(TCItems.FART);
        list.add(TCItems.SAND$STORM);
        list.add(TCItems.BLIZZARD);
        list.add(TCItems.TSUNAMI);
        list.add(TCItems.CLOUD);
        list.add(TCItems.MAY$FLY);
        list.add(TCItems.EFFECT$IMMUNITIES);
        list.add(TCItems.TOTEM$WITH$COOLDOWN);
        list.add(TCItems.LUMINANCE);
        ModLoader.postEvent(new RegisterAccessoriesComponentUpdateEvent.OtherType(list));
    });
    private boolean panicNecklace;
    private transient int remainLavaImmuneTicks;
    private transient int totalLavaImmuneTicks;

    @Override
    public void setToDefaultValue() {
        super.setToDefaultValue();
        this.panicNecklace = false;
        this.remainLavaImmuneTicks = 0;
        this.totalLavaImmuneTicks = 0;
    }

    public boolean hasPanicNecklace() {
        return panicNecklace;
    }

    public void increaseLavaImmuneTicks() {
        if (remainLavaImmuneTicks < totalLavaImmuneTicks) {
            this.remainLavaImmuneTicks++;
        }
    }

    public boolean decreaseLavaImmuneTicks() {
        if (remainLavaImmuneTicks > 0) {
            this.remainLavaImmuneTicks--;
            return true;
        }
        return false;
    }

    @Override
    public void flushAbility(LivingEntity living) {
        super.flushAbility(living);
        CuriosApi.getCuriosInventory(living).ifPresent(handler -> {
            for (ICurioStacksHandler curioStacksHandler : handler.getCurios().values()) {
                IDynamicStackHandler stackHandler = curioStacksHandler.getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    PrimitiveValueComponent component = TCUtils.getAccessoriesComponent(stack);
                    if (component == null) continue;
                    Item item = stack.getItem();

                    compute(component);

                    if (!panicNecklace && item instanceof PanicNecklace) this.panicNecklace = true;
                    LibUtils.forMixin$Inject();
                }
            }
            TagKey<EntityType<?>> ignores = getValue(TCItems.MOB$IGNORE);
            if (!TCTags.NOTHING.equals(ignores)) {
                living.level().getEntitiesOfClass(Mob.class, new AABB(living.blockPosition()).inflate(31.5), mob -> mob.getType().is(ignores)).forEach(mob -> {
                    if (mob.getTarget() == living) mob.setTarget(null);
                });
            }
            this.totalLavaImmuneTicks = getValue(TCItems.LAVA$IMMUNE$TICKS);
        });
        NeoForge.EVENT_BUS.post(new AfterAccessoryAbilitiesFlushedEvent(living));
    }

    @Override
    public <T, V extends PrimitiveValue<T>> void putUnitIfPresent(ValueType<T, V> type) {
        if (UNITS_REQUIRE_UPDATE.contains(type)) {
            super.putUnitIfPresent(type);
        }
    }

    @Override
    public <T, V extends PrimitiveValue<T>> void combineValue(ValueType<T, V> type, V value) {
        if (OTHER_REQUIRE_UPDATE.contains(type)) {
            super.combineValue(type, value);
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = super.serializeNBT(provider);
        if (panicNecklace) {
            nbt.putBoolean("panicNecklace", true);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        this.panicNecklace = nbt.getBoolean("panicNecklace");
    }

    public static AccessoriesAttachment of(Entity entity) {
        return entity.getData(TCAttachments.ACCESSORIES);
    }
}
