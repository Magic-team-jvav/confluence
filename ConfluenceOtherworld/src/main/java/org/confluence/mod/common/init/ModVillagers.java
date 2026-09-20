package org.confluence.mod.common.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;

import static org.confluence.mod.Confluence.MODID;

public final class ModVillagers {
    public static final DeferredRegister<PoiType> POIS = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, MODID);
    public static final DeferredRegister<VillagerType> TYPES = DeferredRegister.create(Registries.VILLAGER_TYPE, MODID);

    // 村民的兴趣点
    public static final RegistryObject<PoiType> SKY_POI = POIS.register("sky", () -> new PoiType(ImmutableSet.copyOf(FunctionalBlocks.SKY_MILL.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static final RegistryObject<PoiType> COOKING_POI = POIS.register("cooking", () -> new PoiType(ImmutableSet.copyOf(FunctionalBlocks.COOKING_POT.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static final RegistryObject<PoiType> COIN_POI = POIS.register("coin", () -> new PoiType(ImmutableSet.copyOf(FunctionalBlocks.SAFE.get().getStateDefinition().getPossibleStates()), 1, 1));

    // 村民的职业
    public static final RegistryObject<VillagerProfession> SKY_MILLER = PROFESSIONS.register("sky_miller", () -> new VillagerProfession("sky", holder -> holder.is(SKY_POI.getId()), holder -> holder.is(SKY_POI.getId()), ImmutableSet.of(MaterialItems.FALLING_STAR.get()), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_WEAPONSMITH));
    public static final RegistryObject<VillagerProfession> CHEF = PROFESSIONS.register("chef", () -> new VillagerProfession("chef", holder -> holder.is(COOKING_POI.getId()), holder -> holder.is(COOKING_POI.getId()), ImmutableSet.of(FoodItems.COOK_FISH.get()), ImmutableSet.of(), SoundEvents.CAMPFIRE_CRACKLE));
    public static final RegistryObject<VillagerProfession> BANKER = PROFESSIONS.register("banker", () -> new VillagerProfession("coin", holder -> holder.is(COIN_POI.getId()), holder -> holder.is(COIN_POI.getId()), ImmutableSet.of(
            ModItems.GOLD_COIN.get(),
            ModItems.PLATINUM_COIN.get(),
            ModItems.EMERALD_COIN.get()
    ), ImmutableSet.of(), ModSoundEvents.COINS.get()));

    // 村民的群系
    public static final RegistryObject<VillagerType> SKY_TYPE = TYPES.register("sky", () -> new VillagerType("confluence_sky")); // 天域村民

    public static void setVillagerType(Villager villager) {
        if (villager.position().y >= 240.0) {
            villager.setVariant(SKY_TYPE.get());
        }
    }

    public static void register(IEventBus eventBus) {
        POIS.register(eventBus);
        PROFESSIONS.register(eventBus);
        TYPES.register(eventBus);
    }
}
