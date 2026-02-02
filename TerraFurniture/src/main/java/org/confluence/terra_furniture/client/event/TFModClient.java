package org.confluence.terra_furniture.client.event;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.api.client.renderer.block.BaseFunctionalGeoBER;
import org.confluence.terra_furniture.api.client.renderer.block.CommonRenderHooks;
import org.confluence.terra_furniture.api.client.renderer.block.MultiRenderTypeGeoBER;
import org.confluence.terra_furniture.client.screen.GlassKilnScreen;
import org.confluence.terra_furniture.client.screen.IceMachineScreen;
import org.confluence.terra_furniture.client.screen.LivingLoomScreen;
import org.confluence.terra_furniture.common.block.light.LargeChandelierBlock;
import org.confluence.terra_furniture.common.block.misc.HangingPotBlock;
import org.confluence.terra_furniture.common.block.misc.PinWheel;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFEntities;
import org.confluence.terra_furniture.common.init.TFRegistries;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.function.Predicate;

/**
 * Register client-only events here, such as renderers and menus.
 */
@EventBusSubscriber(modid = TerraFurniture.MODID, value = Dist.CLIENT)
public final class TFModClient {
    /** 选择层中以flame起始的名字*/
    public static final Predicate<GeoBone> FLAME = geoBone -> geoBone.getName().startsWith("flame");
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        /*--entities renderers--*/
        event.registerEntityRenderer(TFEntities.NULL_RIDE.get(), NoopRenderer::new);

        /*--block entities renderers--*/
        regSimpleGeoBER(event, TFBlocks.PLASTIC_CHAIR_ENTITY, false);
        regSimpleGeoBER(event, TFBlocks.CLOCK_ENTITY, false);
        event.registerBlockEntityRenderer(TFBlocks.PIN_WHEEL_ENTITY.get(),
                context -> BaseFunctionalGeoBER.Builder.<PinWheel.BEntity>of(false)
                        .addOperationBindRule(0, geoBone -> geoBone.getName().equals("bone"),
                                (geoBone, bEntity) ->  geoBone.setRotZ(bEntity.getStepNext())
                        )
                        .build()
        );
        event.registerBlockEntityRenderer(TFBlocks.LARGE_CHANDELIER_ENTITY.get(),
                context ->  MultiRenderTypeGeoBER.Builder.<LargeChandelierBlock.BEntity>ofMRT()
                        .addOperationBindRule(3, FLAME, TFModClient::litControlledHide)
                        .setDefaultRenderType(RenderType::entityCutout)
                        .addRenderRule(3, FLAME, RenderType::text)
                        .canGlow()
                        .addRenderHook(CommonRenderHooks.swaying())
                        .renderBox(pos -> new AABB(pos.getX() -1, pos.getY(), pos.getZ()-1, pos.getX() +1, pos.getY() -1, pos.getZ() +1))
                        .build()
        );
        //event.registerBlockEntityRenderer(TFBlocks.LARGE_CHANDELIER_ENTITY.get(), context -> new TestGlow());
        event.registerBlockEntityRenderer(TFBlocks.HANGING_POT_ENTITY.get(),
                context -> BaseFunctionalGeoBER.Builder.<HangingPotBlock.BEntity>of(false)
                        .addRenderHook(CommonRenderHooks.swaying())
                        .addRenderHook(new HangingPotBlock.AddedRenderer<>())
                        .build()
        );
    }

    /**
     * 由对应方块中的LIT属性控制亮灭。
     * <p><b>注意: 只能应用在具有LIT属性的方块的方块实体上</b></p>
     */
    public static <O extends BlockEntity & GeoBlockEntity> void litControlledHide(GeoBone bone, O entity) {
        bone.setHidden(!entity.getBlockState().getValue(BlockStateProperties.LIT));
    }

    public static <O extends BlockEntity & GeoBlockEntity> void regSimpleGeoBER(EntityRenderersEvent.RegisterRenderers event, DeferredHolder<BlockEntityType<?>, BlockEntityType<O>> holder, boolean isNegativeModel) {
        event.registerBlockEntityRenderer(holder.get(), context -> BaseFunctionalGeoBER.Builder.simple(isNegativeModel));
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TFRegistries.GLASS_KILN_MENU.get(), GlassKilnScreen::new);
        event.register(TFRegistries.LIVING_LOOM_MENU.get(), LivingLoomScreen::new);
        event.register(TFRegistries.ICE_MACHINE_MENU.get(), IceMachineScreen::new);
    }

    @SubscribeEvent
    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        TFRegistries.RECIPE_TYPES.getEntries().forEach(holder -> event.registerRecipeCategoryFinder(holder.get(), recipeHolder -> RecipeBookCategories.UNKNOWN));
    }
}
