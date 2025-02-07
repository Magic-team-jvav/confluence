package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.entity.projectile.BoulderEntity;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.util.CuriosUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class BoulderWorld extends SecretSeed {
    public BoulderWorld(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "boulder".equals(seed);
    }

    public static void createBoulderWhenBlockDestroy(ServerPlayer serverPlayer, BlockState blockState, BlockPos pos) {
        if (ModSecretSeeds.BOULDER_WORLD.match(serverPlayer.server) && serverPlayer.level().random.nextFloat() <= 0.01F) {
            BoulderEntity entity = new BoulderEntity(serverPlayer.serverLevel(), pos.getCenter(), blockState);
            entity.targetTo(serverPlayer);
            entity.getEntityData().set(BoulderEntity.DATA_VERTICAL, false);
            serverPlayer.serverLevel().addFreshEntity(entity);
        }
    }

    public static void forceSetCurseOfBoredomMeteorite(ServerPlayer player) {
        if (ModSecretSeeds.BOULDER_WORLD.match(player.server)) {
            ItemStack stack = CuriosUtils.getSlot(player, TerraCurio.CURIO_SLOT, 0);
            if (stack == null || !stack.is(AccessoryItems.CURSE_OF_BOREDOM_METEORITE.get())) {
                Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
                optional.ifPresent(iCuriosItemHandler -> {
                    ItemStack itemStack = AccessoryItems.CURSE_OF_BOREDOM_METEORITE.get().getDefaultInstance();
                    itemStack.enchant(player.server.registryAccess().holderOrThrow(Enchantments.BINDING_CURSE), 1);
                    iCuriosItemHandler.setEquippedCurio(TerraCurio.CURIO_SLOT, 0, itemStack);
                });
            }
        }
    }
}
