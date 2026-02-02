package com.xiaohunao.enemybanner;

import com.mojang.logging.LogUtils;
import com.xiaohunao.enemybanner.banner.BannerManager;
import com.xiaohunao.enemybanner.blocks.BlockRegister;
import com.xiaohunao.enemybanner.gui.Menus;
import com.xiaohunao.enemybanner.handler.ServerEventHandler;
import com.xiaohunao.enemybanner.items.ItemRegister;
import com.xiaohunao.enemybanner.items.SilksItem;
import com.xiaohunao.enemybanner.renderer.BannerWithoutLevelRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(EnemyBanner.MODID)
public class EnemyBanner {
    public static final String MODID = "enemybanner";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(
            "enemybanner_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.enemybanner_tab"))
                    .icon(ItemRegister.ENEMY_BANNER::toStack)
                    .displayItems(((parameters, output) -> {
                        output.accept(ItemRegister.ENEMY_BANNER);
                        output.accept(ItemRegister.BANNER_BOX);
                        for (DeferredItem<SilksItem> item : ItemRegister.SILKS_MAP.values()) {
                            output.accept(item.get().asItem());
                        }
                    }))
                    .build()
    );

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);

    public static final DeferredHolder<MobEffect, EnemyBannerEffect> ENEMY_BANNER_EFFECT = MOB_EFFECTS.register("enemybanner_effect", () -> new EnemyBannerEffect(MobEffectCategory.BENEFICIAL, 0xffffff));

    public static final TagKey<EntityType<?>> DENIED_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, asResource("denied_entities"));

    public EnemyBanner(IEventBus modEventBus, ModContainer modContainer) {

        BannerParameters.register(modEventBus);
        AttachmentTypeRegister.register(modEventBus);
        BlockRegister.register(modEventBus);
        ItemRegister.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        Menus.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);

        NeoForge.EVENT_BUS.register(ServerEventHandler.class);
        NeoForge.EVENT_BUS.register(BannerManager.class);
        modEventBus.addListener(EnemyBanner::gatherData);
        modEventBus.addListener(EnemyBanner::registerClientExtensions);

        modContainer.registerConfig(ModConfig.Type.COMMON, BannerConfig.SPEC);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
                event.includeClient(),
                new ItemModelProvider(packOutput, EnemyBanner.MODID, existingFileHelper) {
                    @Override
                    protected void registerModels() {
                        ItemRegister.SILKS_MAP.values().forEach(item -> basicItem(item.get()));
                    }
                });
    }


    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final BannerWithoutLevelRenderer bannerWithoutLevelRenderer = new BannerWithoutLevelRenderer();

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return bannerWithoutLevelRenderer;
            }
        }, ItemRegister.ENEMY_BANNER.get(), ItemRegister.ENEMY_BANNER_PLANE.get());
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String asDescriptionId(String path) {
        return MODID + "." + path;
    }

    public static <T> ResourceKey<Registry<T>> asResourceKey(String path) {
        return ResourceKey.createRegistryKey(asResource(path));
    }

    public static <T> ResourceKey<T> asResourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, asResource(path));
    }
}
