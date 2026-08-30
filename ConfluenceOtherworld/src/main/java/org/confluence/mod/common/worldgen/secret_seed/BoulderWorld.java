package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.confluence.mod.common.init.ModSecretSeeds;
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

    @Override
    public boolean isHided() {
        return true;
    }

    public static void createBoulderWhenBlockDestroy(ServerPlayer player, BlockState state, BlockPos pos) {
        if (ModSecretSeeds.BOULDER_WORLD.match(player.server) && LibMathUtils.checkChance(0.01F, player.getRandom())) {
            if (state.getCollisionShape(player.level(), pos) == Shapes.block()) {
                BoulderEntity entity = new BoulderEntity(player.level(), pos.getCenter(), state);
                entity.targetTo(player);
                player.level().addFreshEntity(entity);
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
                    LibUtils.updateItemStackNbt(itemStack, tag -> tag.putBoolean("summoned", true)); // 第一次出生的保护
                    itemStack.enchant(player.server.registryAccess().holderOrThrow(Enchantments.BINDING_CURSE), 1);
                    iCuriosItemHandler.setEquippedCurio(TerraCurio.CURIO_SLOT, 0, itemStack);
                });
            }
        }
    }
}
