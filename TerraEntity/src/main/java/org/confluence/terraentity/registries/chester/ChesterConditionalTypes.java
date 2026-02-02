package org.confluence.terraentity.registries.chester;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.registries.TERegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.Supplier;

public class ChesterConditionalTypes {
    public static DeferredRegister<ChesterConditionalType> TYPES = DeferredRegister.create(TERegistries.Keys.CHESTER_CONDITIONAL_TYPE, TerraEntity.MODID);

    public static final Supplier<ChesterConditionalType> ENDER_CHEST = TYPES.register("routine_container", ()->new ChesterConditionalType(
            5,
            (pos, player, level)-> {
                return level.getBlockEntity(pos) instanceof BaseContainerBlockEntity;
            },
            (pos, player, level)->{
                BlockState state = level.getBlockState(pos);
//                BlockEntity blockEntity = player.level().getBlockEntity(pos);
                return state.getMenuProvider(level, pos);

//                return new SimpleMenuProvider((id, inv, player)-> {

//                    if(state.getValue(ChestBlock.TYPE) == ChestType.SINGLE){
//                        return ChestMenu.threeRows(id, inv, (Container) player.level().getBlockEntity(pos));
//                    }else{
//                        return ChestMenu.sixRows(id, inv, (Container) player.level().getBlockEntity(pos));
//                    }
//                }, Component.literal("Remote Chester Chest"));
            }));

//    public static final Supplier<ChesterConditionalType> SHULKER_CHEST = TYPES.register("shulker_chest", ()->new ChesterConditionalType(
//            6,
//            (pos, player, level)-> player.level().getBlockEntity(pos) instanceof ShulkerBoxBlockEntity,
//            (pos, player1, level)-> new SimpleMenuProvider((id, inv, player)-> new ShulkerBoxMenu(id, inv, (Container) player.level().getBlockEntity(pos)), Component.literal("Remote Shulker Chest"))));


    @Nullable
    public static ChesterConditionalType match(BlockPos pos, Player player, Level level){
        return TERegistries.CHESTER_CONDITIONAL_TYPES.stream()
                .filter(provider -> provider.canOpen(pos, player, level))
                .min(Comparator.comparingInt(ChesterConditionalType::getPriority))
                .orElse(null);
    }
}
