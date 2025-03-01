package org.confluence.mod.integration.ponder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.MaterialItems;

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
        HELPER.forComponents(FunctionalBlocks.DEMON_ALTAR.asItem(),FunctionalBlocks.CRIMSON_ALTAR.asItem())
                .addStoryBoard(Confluence.asResource("functional/altar"), ModPonderPlugin::altar, TAG_GAMEPLAY);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(TAG_GAMEPLAY);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
    }

    public static void altar(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("using_altar", "Using Altar");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        Vec3 altarPoint = new Vec3(2.5, 1.5, 2.5);

        scene.idle(20);
        scene.world().showSection(util.select().fromTo(2,1,2,2,1,2), Direction.DOWN);
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
