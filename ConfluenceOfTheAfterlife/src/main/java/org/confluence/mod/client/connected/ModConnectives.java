package org.confluence.mod.client.connected;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import org.confluence.mod.client.connected.behaviour.ConnectedTextureBehaviour;
import org.confluence.mod.client.connected.behaviour.EncasedCTBehaviour;
import org.confluence.mod.client.connected.behaviour.SimpleCTBehaviour;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class ModConnectives {
    public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();
    public static final CasingConnectivity CASING_CONNECTIVITY = new CasingConnectivity();

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(StitchedSprite::onTextureStitchPost);
        MODEL_SWAPPER.registerListeners(modEventBus);

        // registerCasingConnectivity是用来注册机壳的
        registerCTBehaviour(FunctionalBlocks.ANDESITE_CASING.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING));
        registerCasingConnectivity(FunctionalBlocks.ANDESITE_CASING.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.ANDESITE_CASING));

        registerCTBehaviour(NatureBlocks.THIN_ICE_BLOCK.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.THIN_ICE_BLOCK));
        registerCTBehaviour(DecorativeBlocks.SUN_PLATE.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.SUN_PLATE));
        registerCTBehaviour(DecorativeBlocks.PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.WHITE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.WHITE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIGHT_GRAY_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.GRAY_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.GRAY_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.BLACK_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BLACK_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.BROWN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BROWN_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.RED_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.RED_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.ORANGE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.ORANGE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.YELLOW_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.YELLOW_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.LIME_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIME_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.GREEN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.GREEN_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.CYAN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.CYAN_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIGHT_BLUE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.BLUE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BLUE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.PURPLE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PURPLE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.MAGENTA_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.MAGENTA_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.PINK_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PINK_PURE_GLASS));
    }

    private static <T extends Block> void registerCasingConnectivity(T entry, BiConsumer<T, CasingConnectivity> consumer) {
        consumer.accept(entry, CASING_CONNECTIVITY);
    }

    private static void registerCTBehaviour(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier) {
        ConnectedTextureBehaviour behavior = behaviorSupplier.get();
        MODEL_SWAPPER.getCustomBlockModels().register(BuiltInRegistries.BLOCK.getKey(entry), model -> new CTModel(model, behavior));
    }
}
