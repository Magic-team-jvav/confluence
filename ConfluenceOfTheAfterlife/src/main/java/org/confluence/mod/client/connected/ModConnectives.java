package org.confluence.mod.client.connected;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import org.confluence.mod.client.connected.behaviour.ConnectedTextureBehaviour;
import org.confluence.mod.client.connected.behaviour.EncasedCTBehaviour;
import org.confluence.mod.client.connected.behaviour.SimpleCTBehaviour;
import org.confluence.mod.client.connected.randomize.RandomizeCTModel;
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
        registerRandom(FunctionalBlocks.ANDESITE_CASING.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING), 2);
        registerCasingConnectivity(FunctionalBlocks.ANDESITE_CASING.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.ANDESITE_CASING));

        register(NatureBlocks.THIN_ICE_BLOCK.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.THIN_ICE_BLOCK));
        register(DecorativeBlocks.SUN_PLATE.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.SUN_PLATE));
        register(DecorativeBlocks.PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PURE_GLASS));
        register(DecorativeBlocks.WHITE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.WHITE_PURE_GLASS));
        register(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIGHT_GRAY_PURE_GLASS));
        register(DecorativeBlocks.GRAY_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.GRAY_PURE_GLASS));
        register(DecorativeBlocks.BLACK_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BLACK_PURE_GLASS));
        register(DecorativeBlocks.BROWN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BROWN_PURE_GLASS));
        register(DecorativeBlocks.RED_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.RED_PURE_GLASS));
        register(DecorativeBlocks.ORANGE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.ORANGE_PURE_GLASS));
        register(DecorativeBlocks.YELLOW_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.YELLOW_PURE_GLASS));
        register(DecorativeBlocks.LIME_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIME_PURE_GLASS));
        register(DecorativeBlocks.GREEN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.GREEN_PURE_GLASS));
        register(DecorativeBlocks.CYAN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.CYAN_PURE_GLASS));
        register(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIGHT_BLUE_PURE_GLASS));
        register(DecorativeBlocks.BLUE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BLUE_PURE_GLASS));
        register(DecorativeBlocks.PURPLE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PURPLE_PURE_GLASS));
        register(DecorativeBlocks.MAGENTA_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.MAGENTA_PURE_GLASS));
        register(DecorativeBlocks.PINK_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PINK_PURE_GLASS));
    }

    private static <T extends Block> void registerCasingConnectivity(T entry, BiConsumer<T, CasingConnectivity> consumer) {
        consumer.accept(entry, CASING_CONNECTIVITY);
    }

    private static void register(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier) {
        MODEL_SWAPPER.getCustomBlockModels().register(BuiltInRegistries.BLOCK.getKey(entry), model -> new CTModel(model, behaviorSupplier.get()));
    }

    private static void registerRandom(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier, int width) {
        MODEL_SWAPPER.getCustomBlockModels().register(BuiltInRegistries.BLOCK.getKey(entry), model -> new RandomizeCTModel(model, behaviorSupplier.get(), width));
    }
}
