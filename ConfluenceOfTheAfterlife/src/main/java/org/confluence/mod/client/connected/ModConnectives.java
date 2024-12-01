package org.confluence.mod.client.connected;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import org.confluence.mod.client.connected.behaviour.ConnectedTextureBehaviour;
import org.confluence.mod.client.connected.behaviour.EncasedCTBehaviour;
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

        registerCTBehaviour(FunctionalBlocks.ANDESITE_CASING.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING));
        registerCasingConnectivity(FunctionalBlocks.ANDESITE_CASING.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.ANDESITE_CASING));

        registerCTBehaviour(NatureBlocks.THIN_ICE_BLOCK.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.THIN_ICE_BLOCK));
        registerCasingConnectivity(NatureBlocks.THIN_ICE_BLOCK.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.THIN_ICE_BLOCK));

        registerCTBehaviour(DecorativeBlocks.SUN_PLATE.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.SUN_PLATE));
        registerCasingConnectivity(DecorativeBlocks.SUN_PLATE.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.SUN_PLATE));

        registerCTBehaviour(DecorativeBlocks.PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.WHITE_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.WHITE_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.WHITE_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.WHITE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.LIGHT_GRAY_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.LIGHT_GRAY_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.GRAY_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.GRAY_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.GRAY_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.GRAY_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.BLACK_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.BLACK_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.BLACK_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.BLACK_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.BROWN_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.BROWN_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.BROWN_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.BROWN_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.RED_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.RED_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.RED_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.RED_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.ORANGE_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.ORANGE_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.ORANGE_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.ORANGE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.YELLOW_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.YELLOW_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.YELLOW_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.YELLOW_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.LIME_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.LIME_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.LIME_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.LIME_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.GREEN_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.GREEN_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.GREEN_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.GREEN_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.CYAN_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.CYAN_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.CYAN_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.CYAN_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.LIGHT_BLUE_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.LIGHT_BLUE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.BLUE_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.BLUE_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.BLUE_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.BLUE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.PURPLE_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.PURPLE_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.PURPLE_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.PURPLE_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.MAGENTA_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.MAGENTA_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.MAGENTA_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.MAGENTA_PURE_GLASS));
        registerCTBehaviour(DecorativeBlocks.PINK_PURE_GLASS.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.PINK_PURE_GLASS));
        registerCasingConnectivity(DecorativeBlocks.PINK_PURE_GLASS.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.PINK_PURE_GLASS));



    }

    private static <T extends Block> void registerCasingConnectivity(T entry, BiConsumer<T, CasingConnectivity> consumer) {
        consumer.accept(entry, CASING_CONNECTIVITY);
    }

    private static void registerCTBehaviour(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier) {
        ConnectedTextureBehaviour behavior = behaviorSupplier.get();
        MODEL_SWAPPER.getCustomBlockModels().register(BuiltInRegistries.BLOCK.getKey(entry), model -> new CTModel(model, behavior));
    }
}
