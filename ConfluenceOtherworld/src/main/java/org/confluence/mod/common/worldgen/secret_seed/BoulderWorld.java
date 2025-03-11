package org.confluence.mod.common.worldgen.secret_seed;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.util.CuriosUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class BoulderWorld extends SecretSeed {
    public BoulderWorld(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "redigit".equals(seed);
    }

    public static void createBoulderWhenBlockDestroy(ServerPlayer serverPlayer, BlockState blockState, BlockPos pos) {
        if (ModSecretSeeds.BOULDER_WORLD.match(serverPlayer.server) && serverPlayer.level().random.nextFloat() <= 0.01F) {
            if (blockState.getCollisionShape(serverPlayer.level(), pos) == Shapes.block()) {
                BoulderEntity entity = new BoulderEntity(serverPlayer.serverLevel(), pos.getCenter(), blockState);
                entity.targetTo(serverPlayer);
                entity.setVertical(false);
                serverPlayer.serverLevel().addFreshEntity(entity);
            }
        }
    }

    public static void forceSetAccessory(ServerPlayer player) {
        if (ModSecretSeeds.BOULDER_WORLD.match(player.server)) {
            ItemStack stack = CuriosUtils.getSlot(player, TerraCurio.CURIO_SLOT, 0);
            if (stack == null || !stack.is(ModItems.BOREDOMS_PACT_FALLING_RESOLVE.get())) {
                Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
                optional.ifPresent(iCuriosItemHandler -> {
                    ItemStack itemStack = ModItems.BOREDOMS_PACT_FALLING_RESOLVE.get().getDefaultInstance();
                    itemStack.enchant(player.server.registryAccess().holderOrThrow(Enchantments.BINDING_CURSE), 1);
                    iCuriosItemHandler.setEquippedCurio(TerraCurio.CURIO_SLOT, 0, itemStack);
                });
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderBoulderSun(Minecraft minecraft) {
        if (ModSecretSeeds.BOULDER_WORLD.match()) {
            MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            BlockState blockState = FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState();
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(Axis.ZP.rotation(minecraft.level.getTimeOfDay(0) * Mth.TWO_PI));
            poseStack.translate(-5, 100, -5);
            poseStack.scale(10, 10, 10);
            minecraft.getBlockRenderer().renderSingleBlock(blockState, poseStack, bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
        }
    }
}
