package org.confluence.mod.client.effect.connected;

import net.minecraft.world.level.block.Block;
import org.confluence.mod.client.effect.connected.behaviour.ConnectedTextureBehaviour;
import org.confluence.mod.client.effect.connected.behaviour.EncasedCTBehaviour;
import org.confluence.mod.client.effect.connected.behaviour.SimpleCTBehaviour;
import org.confluence.mod.client.effect.connected.custom.PillarCTModel;
import org.confluence.mod.client.effect.connected.custom.RandomizeCTModel;
import org.confluence.mod.client.effect.connected.custom.WeightedCTModel;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ModConnectives {
    public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();
    public static final CasingConnectivity CASING_CONNECTIVITY = new CasingConnectivity();

    public static void register() {
        register(ModBlocks.ANDESITE_CASING.get(), () -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING));
        registerCasingConnectivity(ModBlocks.ANDESITE_CASING.get(), (block, cc) -> cc.makeCasing(block, AllSpriteShifts.ANDESITE_CASING));

        register(DecorativeBlocks.SUN_PLATE.FULL.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/plate/sun_plate", "sun_plate", 1));
        registerRandomize(DecorativeBlocks.MOON_PLATE.FULL.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/plate/moon_plate", "moon_plate", 10), 10);
        registerWeighted(DecorativeBlocks.BLUE_GEL_BLOCK.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/gel/blue_gel_block", "blue_gel_block", 2), 5, 1);
        registerWeighted(DecorativeBlocks.FROZEN_GEL_BLOCK.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/gel/frozen_gel_block", "frozen_gel_block", 2), 5, 1);
        register(FunctionalBlocks.ECHO_BLOCK.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "functional/mechanism/echo_block", "echo_block", 1));

        register(NatureBlocks.PUPIL_BLOCK.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "crimson/pupil_block", "pupil_block", 1));
        register(NatureBlocks.EYEBALL_BLOCK.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "crimson/eyeball_block", "eyeball_block", 1));

        register(NatureBlocks.PALM_LOG_BLOCKS.CHISELED_PLANKS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "wood/palm/chiseled_palm_planks", "chiseled_palm_planks", 1));
        register(DecorativeBlocks.GRANITE_COLUMN.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "granite_cave/granite_column", "granite_column", 1));
        register(DecorativeBlocks.MARBLE_COLUMN.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "marble_cave/marble_column", "marble_column", 1));

        register(DecorativeBlocks.WHITE_PAPER_PANE.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/paper/white_paper_pane", "white_paper_pane", 1));
        register(DecorativeBlocks.WHITE_PAPER_PANE_LAMP.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/paper/white_paper_pane_lamp", "white_paper_pane_lamp", 1));
        register(DecorativeBlocks.MALACHITE_PAPER_PANE_LAMP.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/paper/malachite_paper_pane_lamp", "malachite_paper_pane_lamp", 1));
        register(DecorativeBlocks.MALACHITE_PAPER_PANE.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/paper/malachite_paper_pane", "malachite_paper_pane", 1));

        registerWeighted(DecorativeBlocks.WHITE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/white_pure_glass", "white_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/light_gray_pure_glass", "light_gray_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.GRAY_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/gray_pure_glass", "gray_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.BLACK_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/black_pure_glass", "black_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.BROWN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/brown_pure_glass", "brown_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.RED_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/red_pure_glass", "red_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.ORANGE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/orange_pure_glass", "orange_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.YELLOW_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/yellow_pure_glass", "yellow_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.LIME_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/lime_pure_glass", "lime_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.GREEN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/green_pure_glass", "green_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.CYAN_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/cyan_pure_glass", "cyan_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/light_blue_pure_glass", "light_blue_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.BLUE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/blue_pure_glass", "blue_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.PURPLE_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/purple_pure_glass", "purple_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.MAGENTA_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/magenta_pure_glass", "magenta_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.PINK_PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/pink_pure_glass", "pink_pure_glass", 2), 1, 5);
        registerWeighted(DecorativeBlocks.PURE_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/pure_glass", "pure_glass", 2), 1, 5);

        register(DecorativeBlocks.SOUL_GLASS.get(), () -> new SimpleCTBehaviour(AllCTTypes.OMNIDIRECTIONAL, "decorative/glass/soul_glass", "soul_glass", 1));

        registerPillar(NatureBlocks.BALLOON_MELON.get(), AllCTTypes.OMNIDIRECTIONAL, "crops/balloon_melon", "balloon_melon");
        registerPillar(DecorativeBlocks.WHITE_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/white_balloon", "white_balloon");
        registerPillar(DecorativeBlocks.LIGHT_GRAY_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/light_gray_balloon", "light_gray_balloon");
        registerPillar(DecorativeBlocks.GRAY_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/gray_balloon", "gray_balloon");
        registerPillar(DecorativeBlocks.BLACK_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/black_balloon", "black_balloon");
        registerPillar(DecorativeBlocks.BROWN_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/brown_balloon", "brown_balloon");
        registerPillar(DecorativeBlocks.RED_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/red_balloon", "red_balloon");
        registerPillar(DecorativeBlocks.ORANGE_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/orange_balloon", "orange_balloon");
        registerPillar(DecorativeBlocks.YELLOW_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/yellow_balloon", "yellow_balloon");
        registerPillar(DecorativeBlocks.LIME_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/lime_balloon", "lime_balloon");
        registerPillar(DecorativeBlocks.GREEN_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/green_balloon", "green_balloon");
        registerPillar(DecorativeBlocks.CYAN_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/cyan_balloon", "cyan_balloon");
        registerPillar(DecorativeBlocks.LIGHT_BLUE_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/light_blue_balloon", "light_blue_balloon");
        registerPillar(DecorativeBlocks.BLUE_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/blue_balloon", "blue_balloon");
        registerPillar(DecorativeBlocks.PURPLE_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/purple_balloon", "purple_balloon");
        registerPillar(DecorativeBlocks.MAGENTA_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/magenta_balloon", "magenta_balloon");
        registerPillar(DecorativeBlocks.PINK_BALLOON.get(), AllCTTypes.OMNIDIRECTIONAL, "decorative/balloon/pink_balloon", "pink_balloon");
    }

    /// 目前仅用来注册机壳
    ///
    /// @param entry    注册连接材质的方块
    /// @param consumer 所注册的连接行为
    private static <T extends Block> void registerCasingConnectivity(T entry, BiConsumer<T, CasingConnectivity> consumer) {
        consumer.accept(entry, CASING_CONNECTIVITY);
    }

    /// 注册常规连接材质
    ///
    /// @param entry            注册连接材质的方块
    /// @param behaviorSupplier 所注册的连接行为
    private static void register(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier) {
        MODEL_SWAPPER.getCustomBlockModels().register(entry, model -> new CTModel(model, behaviorSupplier.get()));
    }

    /// 注册随机连接材质
    ///
    /// @param entry            注册连接材质的方块
    /// @param behaviorSupplier 所注册的连接行为
    /// @param width            可选的数量，即贴图的宽高比。如256x128的贴图是2
    private static void registerRandomize(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier, int width) {
        MODEL_SWAPPER.getCustomBlockModels().register(entry, model -> new RandomizeCTModel(model, behaviorSupplier.get(), width));
    }

    /// 注册权重连接材质
    ///
    /// @param entry            注册连接材质的方块
    /// @param behaviorSupplier 所注册的连接行为
    /// @param weights          一个权重数组
    private static void registerWeighted(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier, int... weights) {
        MODEL_SWAPPER.getCustomBlockModels().register(entry, model -> new WeightedCTModel(model, behaviorSupplier.get(), weights));
    }

    /// 注册top bottom side的连接材质模型
    ///
    /// @param entry            注册连接材质的方块
    /// @param type             连接类型
    /// @param blockTextureName 块贴图名称
    /// @param connectedTextureName 连接贴图名称
    private static void registerPillar(Block entry, CTType type, String blockTextureName, String connectedTextureName) {
        MODEL_SWAPPER.getCustomBlockModels().register(entry, model -> new PillarCTModel(model, type, blockTextureName, connectedTextureName));
    }
}
