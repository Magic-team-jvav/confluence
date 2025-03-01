package org.confluence.mod.integration.ponder;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ToolItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ModPonderPlugin implements PonderPlugin {
    private static final ResourceLocation TAG_GAMEPLAY = Confluence.asResource("ponder/gameplay");

    @Override
    public String getModId() {
        return Confluence.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<Item> HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);
        HELPER.forComponents(FunctionalBlocks.DEMON_ALTAR.asItem(), FunctionalBlocks.CRIMSON_ALTAR.asItem())
                .addStoryBoard(Confluence.asResource("functional/altar"), ModPonderPlugin::altar, TAG_GAMEPLAY);
        HELPER.forComponents(ToolItems.RED_WRENCH.get())
                .addStoryBoard(Confluence.asResource("functional/connect"), ModPonderPlugin::connect, TAG_GAMEPLAY);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(TAG_GAMEPLAY).addToIndex();
    }

    private static void connect(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("connecting", "Connecting");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        ItemStack wrench = ToolItems.RED_WRENCH.get().getDefaultInstance();
        Vec3 switchPoint = new Vec3(3.5, 1.5, 1.5);
        Vec3 dartPoint = new Vec3(1.5, 1.5, 3.5);

        scene.idle(20);
        scene.world().showSection(util.select().layers(1, 1), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(switchPoint, Pointing.DOWN, 20).withItem(wrench);
        scene.idle(40);
        scene.overlay().showControls(dartPoint, Pointing.DOWN, 20).withItem(wrench);
        scene.idle(5);
        scene.world().modifyBlockEntity(new BlockPos(1,1,3), AbstractMechanicalBlock.Entity.class, blockEntity -> {
            BlockPos switchPos = new BlockPos(3, 1, 1);
            BlockEntity be = scene.getScene().getWorld().getBlockEntity(switchPos);
            if (be instanceof INetworkEntity entity) {
                blockEntity.connectTo(0xFF0000, switchPos, entity);
            }
        });
        scene.idle(40);
    }

    public static void altar(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("using_altar", "Using Altar");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        Vec3 altarPoint = new Vec3(2.5, 1.5, 2.5);

        scene.idle(20);
        scene.world().showSection(util.select().fromTo(2, 1, 2, 2, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(altarPoint, Pointing.RIGHT, 40).rightClick().withItem(MaterialItems.LENS.toStack());
        scene.overlay().showText(40).text("Right click with item to store materials").pointAt(altarPoint).attachKeyFrame(); // 存物品
        scene.idle(60);
        scene.overlay().showControls(altarPoint, Pointing.RIGHT, 40).rightClick().whileSneaking();
        scene.overlay().showText(40).text("Right click while sneaking to take materials").pointAt(altarPoint).attachKeyFrame(); // 取物品
        scene.idle(60);
        scene.overlay().showControls(altarPoint, Pointing.RIGHT, 40).leftClick();
        scene.overlay().showText(40).text("Left click to crafting once").pointAt(altarPoint).attachKeyFrame(); // 合成一个
        scene.idle(60);
        scene.overlay().showControls(altarPoint, Pointing.RIGHT, 40).leftClick().whileSneaking();
        scene.overlay().showText(40).text("Left click while sneaking to quick crafting").pointAt(altarPoint).attachKeyFrame(); // 合成所有
        scene.idle(50);
    }
}
