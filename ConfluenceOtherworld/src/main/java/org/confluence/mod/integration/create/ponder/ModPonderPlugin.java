package org.confluence.mod.integration.create.ponder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.mod.common.block.functional.DartTrapBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.common.DungeonCompass;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ModPonderPlugin implements PonderPlugin {
    private static final ResourceLocation TAG_MECHANICAL = Confluence.asResource("ponder/mechanical");
    private static final ResourceLocation TAG_GAMEPLAY = Confluence.asResource("ponder/gameplay");

    @Override
    public String getModId() {
        return Confluence.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<Item> HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);
        HELPER.forComponents(FunctionalBlocks.DEMON_ALTAR.asItem(), FunctionalBlocks.CRIMSON_ALTAR.asItem())
                .addStoryBoard(Confluence.asResource("gameplay/altar"), ModPonderPlugin::altar, TAG_GAMEPLAY);
        HELPER.forComponents(ToolItems.METEOR_COMPASS.get())
                .addStoryBoard(Confluence.asResource("gameplay/dungeon"), ModPonderPlugin::dungeon, TAG_GAMEPLAY);
        HELPER.forComponents(ToolItems.RED_WRENCH.get())
                .addStoryBoard(Confluence.asResource("mechanical/connect"), ModPonderPlugin::connect, TAG_MECHANICAL);
        HELPER.forComponents(FunctionalBlocks.SWITCH.asItem())
                .addStoryBoard(Confluence.asResource("mechanical/execute"), ModPonderPlugin::execute, TAG_MECHANICAL);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<Item> HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);
        helper.registerTag(TAG_MECHANICAL)
                .item(ToolItems.RED_WRENCH.get())
                .title("Mechanical").description("Mechanical utils")
                .addToIndex().register();
        helper.registerTag(TAG_GAMEPLAY)
                .item(FunctionalBlocks.DEMON_ALTAR.asItem())
                .title("Gameplay").title("Gameplay utils")
                .addToIndex().register();

        HELPER.addToTag(TAG_MECHANICAL)
                .add(ToolItems.RED_WRENCH.get())
                .add(FunctionalBlocks.SWITCH.asItem());
        HELPER.addToTag(TAG_GAMEPLAY)
                .add(FunctionalBlocks.DEMON_ALTAR.asItem())
                .add(FunctionalBlocks.CRIMSON_ALTAR.asItem())
                .add(ToolItems.METEOR_COMPASS.get());
    }

    private static void connect(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("connecting", "Connecting");
        scene.showBasePlate();
        ItemStack wrench = ToolItems.RED_WRENCH.get().getDefaultInstance();
        Vec3 switchPoint = new Vec3(3.5, 1.5, 1.5);
        Vec3 dartPoint = new Vec3(1.5, 1.5, 3.5);

        scene.idle(20);
        scene.world().showSection(util.select().layers(1, 1), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(switchPoint, Pointing.DOWN, 20).withItem(wrench);
        scene.overlay().showText(40).text("Select first mechanical block").pointAt(switchPoint).attachKeyFrame();
        scene.idle(40);
        scene.overlay().showControls(dartPoint, Pointing.DOWN, 20).withItem(wrench);
        scene.overlay().showText(40).text("Select second mechanical block").pointAt(dartPoint).attachKeyFrame();
        scene.idle(5);
        scene.world().modifyBlockEntity(new BlockPos(1, 1, 3), AbstractMechanicalBlock.BEntity.class, blockEntity -> {
            BlockPos switchPos = new BlockPos(3, 1, 1);
            if (scene.getScene().getWorld().getBlockEntity(switchPos) instanceof INetworkEntity entity) {
                blockEntity.connectTo(0xFF0000, switchPos, entity);
            }
        });
        scene.idle(35);
    }

    private static void execute(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("executing", "Executing");
        scene.world().showSection(util.select().fromTo(0, 0, 0, 4, 2, 4), Direction.UP);
        BlockPos switchPos = new BlockPos(3, 1, 1);
        scene.world().modifyBlockEntity(new BlockPos(1, 1, 3), AbstractMechanicalBlock.BEntity.class, blockEntity -> {
            if (scene.getScene().getWorld().getBlockEntity(switchPos) instanceof INetworkEntity entity) {
                blockEntity.connectTo(0xFF0000, switchPos, entity);
            }
        });
        scene.idle(20);
        scene.world().modifyBlock(switchPos, blockState -> blockState.cycle(StateProperties.SIGNAL), false);
        scene.world().createEntity(level -> {
            Arrow arrow = new Arrow(level, 1.5, 1.5, 2, DartTrapBlock.PICKUP_ITEM_STACK, null);
            arrow.shoot(0, 0, -1, 1, 0);
            return arrow;
        });
        scene.idle(20);
    }

    private static void altar(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("using_altar", "Using Altar");
        scene.showBasePlate();
        Vec3 altarPoint = new Vec3(2.5, 1.5, 2.5);

        scene.idle(20);
        scene.world().showSection(util.select().fromTo(2, 1, 2, 2, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(altarPoint, Pointing.RIGHT, 40).rightClick().withItem(MaterialItems.LENS.toStack());
        scene.overlay().showText(40).text("Right click with item to store materials").pointAt(altarPoint).attachKeyFrame();
        scene.idle(60);
        scene.overlay().showControls(altarPoint, Pointing.RIGHT, 40).rightClick().whileSneaking();
        scene.overlay().showText(40).text("Right click while sneaking to take materials").pointAt(altarPoint).attachKeyFrame();
        scene.idle(60);
        scene.overlay().showControls(altarPoint, Pointing.RIGHT, 40).leftClick();
        scene.overlay().showText(40).text("Left click to crafting once").pointAt(altarPoint).attachKeyFrame();
        scene.idle(60);
        scene.overlay().showControls(altarPoint, Pointing.RIGHT, 40).leftClick().whileSneaking();
        scene.overlay().showText(40).text("Left click while sneaking to quick crafting").pointAt(altarPoint).attachKeyFrame();
        scene.idle(50);
    }

    private static void dungeon(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("dungeon_compass", "Create Dungeon Compass");
        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(2, 1, 2, 4, 1, 4), Direction.DOWN);
        scene.idle(20);
        for (int[] crystal : DungeonCompass.CRYSTALS) {
            int x = 3 - crystal[0];
            int z = 3 - crystal[1];
            scene.world().showSection(util.select().fromTo(x, 1, z, x, 2, z), Direction.DOWN);
            scene.idle(5);
        }
        scene.idle(15);
        scene.overlay().showControls(new Vec3(3.5, 1.5, 3.5), Pointing.DOWN, 20).withItem(ToolItems.METEOR_COMPASS.toStack());
        scene.idle(40);
        scene.world().createItemEntity(new Vec3(3.5, 2, 3.5), new Vec3(0, 0.2, 0), ToolItems.DUNGEON_COMPASS.toStack());
        scene.idle(20);
    }
}
