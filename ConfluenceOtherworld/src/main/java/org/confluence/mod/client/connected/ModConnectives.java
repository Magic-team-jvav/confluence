package org.confluence.mod.client.connected;

import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import org.confluence.mod.client.connected.behaviour.ConnectedTextureBehaviour;
import org.confluence.mod.client.connected.behaviour.EncasedCTBehaviour;
import org.confluence.mod.client.connected.behaviour.SimpleCTBehaviour;
import org.confluence.mod.client.connected.custom.RandomizeCTModel;
import org.confluence.mod.client.connected.custom.WeightedCTModel;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class ModConnectives {
    public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();
    public static final CasingConnectivity CASING_CONNECTIVITY = new CasingConnectivity();

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(StitchedSprite::onTextureStitchPost);
        MODEL_SWAPPER.registerListeners(modEventBus);

        register(ModBlocks.ANDESITE_CASING.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING));
        registerCasingConnectivity(ModBlocks.ANDESITE_CASING.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.ANDESITE_CASING));

        register(DecorativeBlocks.SUN_PLATE.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.SUN_PLATE));
        register(DecorativeBlocks.BLUE_GEL_BLOCK.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BLUE_GEL_BLOCK));
        register(FunctionalBlocks.ECHO_BLOCK.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.ECHO_BLOCK));

        register(DecorativeBlocks.CHISELED_PALM_PLANKS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.CHISELED_PALM_PLANKS));
        register(DecorativeBlocks.GRANITE_COLUMN.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.GRANITE_COLUMN));
        register(DecorativeBlocks.MARBLE_COLUMN.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.CALCITE_COLUMN));

        registerWeighted(DecorativeBlocks.WHITE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.WHITE_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIGHT_GRAY_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.GRAY_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.GRAY_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.BLACK_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BLACK_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.BROWN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BROWN_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.RED_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.RED_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.ORANGE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.ORANGE_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.YELLOW_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.YELLOW_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.LIME_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIME_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.GREEN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.GREEN_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.CYAN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.CYAN_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.LIGHT_BLUE_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.BLUE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.BLUE_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.PURPLE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PURPLE_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.MAGENTA_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.MAGENTA_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.PINK_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PINK_PURE_GLASS), 1, 5);
        registerWeighted(DecorativeBlocks.PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllSpriteShifts.PURE_GLASS), 1, 5);
    }

    /**
     * 目前仅用来注册机壳
     *
     * @param entry    注册连接材质的方块
     * @param consumer 所注册的连接行为
     */
    private static <T extends Block> void registerCasingConnectivity(T entry, BiConsumer<T, CasingConnectivity> consumer) {
        consumer.accept(entry, CASING_CONNECTIVITY);
    }

    /**
     * 注册常规连接材质
     *
     * @param entry            注册连接材质的方块
     * @param behaviorSupplier 所注册的连接行为
     */
    private static void register(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier) {
        MODEL_SWAPPER.getCustomBlockModels().register(entry, model -> new CTModel(model, behaviorSupplier.get()));
    }

    /**
     * 注册随机连接材质
     *
     * @param entry            注册连接材质的方块
     * @param behaviorSupplier 所注册的连接行为
     * @param width            可选的数量，即贴图的宽高比。如256x128的贴图是2
     */
    private static void registerRandomize(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier, int width) {
        MODEL_SWAPPER.getCustomBlockModels().register(entry, model -> new RandomizeCTModel(model, behaviorSupplier.get(), width));
    }

    /**
     * 注册权重连接材质
     *
     * @param entry            注册连接材质的方块
     * @param behaviorSupplier 所注册的连接行为
     * @param weights          一个权重数组
     */
    private static void registerWeighted(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier, int... weights) {
        MODEL_SWAPPER.getCustomBlockModels().register(entry, model -> new WeightedCTModel(model, behaviorSupplier.get(), weights));
    }
}
