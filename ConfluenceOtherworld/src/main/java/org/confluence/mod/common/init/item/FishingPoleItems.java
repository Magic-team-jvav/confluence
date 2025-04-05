package org.confluence.mod.common.init.item;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.fishing.*;

import java.util.function.Supplier;

public class FishingPoleItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<AbstractFishingPole> WOOD_FISHING_POLE = register("wood_fishing_pole", WoodFishingPole::new),
            REINFORCED_FISHING_POLE = register("reinforced_fishing_pole", ReinforcedFishingPole::new),
            FISHER_OF_SOULS = register("fisher_of_souls", FisherOfSouls::new),
            FLESHCATCHER = register("fleshcatcher", Fleshcatcher::new),
            SCARAB_FISHING_ROD = register("scarab_fishing_rod", ScarabFishingRod::new),
            CHUM_CASTER = register("chum_caster", ChumCaster::new),
            FIBERGLASS_FISHING_POLE = register("fiberglass_fishing_pole", FiberglassFishingPole::new),
            MECHANICS_ROD = register("mechanics_rod", MechanicsRod::new),
            SITTING_DUCKS_FISHING_POLE = register("sitting_ducks_fishing_pole", SittingDucksFishingPole::new),
            HOTLINE_FISHING_HOOK = register("hotline_fishing_hook", HotlineFishingHookItem::new),
            GOLDEN_FISHING_ROD = register("golden_fishing_rod", GoldenFishingRod::new);

    private static DeferredItem<AbstractFishingPole> register(String name, Supplier<AbstractFishingPole> supplier) {
        return ITEMS.register(name, supplier);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerCast() {
        ResourceLocation cast = ResourceLocation.withDefaultNamespace("cast");
        ClampedItemPropertyFunction function = (itemStack, level, living, speed) -> {
            if (living == null) {
                return 0.0F;
            } else {
                boolean flag = living.getMainHandItem() == itemStack;
                boolean flag1 = living.getOffhandItem() == itemStack;
                if (living.getMainHandItem().getItem() instanceof FishingRodItem) flag1 = false;
                return (flag || flag1) && living instanceof Player && ((Player) living).fishing != null ? 1.0F : 0.0F;
            }
        };
        ITEMS.getEntries().forEach(pole -> ItemProperties.register(pole.get(), cast, function));
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
