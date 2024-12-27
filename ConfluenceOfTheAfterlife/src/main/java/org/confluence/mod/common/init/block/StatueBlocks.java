package org.confluence.mod.common.init.block;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.StatueBlock;
import org.confluence.mod.common.block.functional.BehaviourStatueBlock;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.SwordItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

@SuppressWarnings("unused")
public class StatueBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    static List<Supplier<BehaviourStatueBlock>> BEHAVIOUR_STATUES = new ArrayList<>();

    public static final Supplier<StatueBlock> A_STATUE = register("a_statue");
    public static final Supplier<StatueBlock> B_STATUE = register("b_statue");
    public static final Supplier<StatueBlock> C_STATUE = register("c_statue");
    public static final Supplier<StatueBlock> D_STATUE = register("d_statue");
    public static final Supplier<StatueBlock> E_STATUE = register("e_statue");
    public static final Supplier<StatueBlock> F_STATUE = register("f_statue");
    public static final Supplier<StatueBlock> G_STATUE = register("g_statue");
    public static final Supplier<StatueBlock> H_STATUE = register("h_statue");
    public static final Supplier<StatueBlock> I_STATUE = register("i_statue");
    public static final Supplier<StatueBlock> J_STATUE = register("j_statue");
    public static final Supplier<StatueBlock> K_STATUE = register("k_statue");
    public static final Supplier<StatueBlock> L_STATUE = register("l_statue");
    public static final Supplier<StatueBlock> M_STATUE = register("m_statue");
    public static final Supplier<StatueBlock> N_STATUE = register("n_statue");
    public static final Supplier<StatueBlock> O_STATUE = register("o_statue");
    public static final Supplier<StatueBlock> P_STATUE = register("p_statue");
    public static final Supplier<StatueBlock> Q_STATUE = register("q_statue");
    public static final Supplier<StatueBlock> R_STATUE = register("r_statue");
    public static final Supplier<StatueBlock> S_STATUE = register("s_statue");
    public static final Supplier<StatueBlock> T_STATUE = register("t_statue");
    public static final Supplier<StatueBlock> U_STATUE = register("u_statue");
    public static final Supplier<StatueBlock> V_STATUE = register("v_statue");
    public static final Supplier<StatueBlock> W_STATUE = register("w_statue");
    public static final Supplier<StatueBlock> X_STATUE = register("x_statue");
    public static final Supplier<StatueBlock> Y_STATUE = register("y_statue");
    public static final Supplier<StatueBlock> Z_STATUE = register("z_statue");

    public static final Supplier<BehaviourStatueBlock> ARMED_ZOMBIE_STATUE = registerBehaviour("armed_zombie_statue", new BehaviourStatueBlock.SummonBehaviour(false, (level, pos) -> {
        Zombie zombie = new Zombie(level);
        zombie.setPos(pos);
        zombie.setItemInHand(InteractionHand.MAIN_HAND, SwordItems.ZOMBIE_ARM.toStack());
        return zombie;
    }));

    public static final Supplier<BlockEntityType<BehaviourStatueBlock.Entity>> BLOCK_ENTITY = BLOCK_ENTITIES.register("behaviour_statue_entity", () -> {
        BehaviourStatueBlock[] validBlocks = BEHAVIOUR_STATUES.stream().map(Supplier::get).toArray(BehaviourStatueBlock[]::new);
        BEHAVIOUR_STATUES = null;
        return BlockEntityType.Builder.of(BehaviourStatueBlock.Entity::new, validBlocks).build(null);
    });

    private static Supplier<StatueBlock> register(String id) {
        DeferredBlock<StatueBlock> block = BLOCKS.register(id, () -> new StatueBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
        ModItems.BLOCK_ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static Supplier<BehaviourStatueBlock> registerBehaviour(String id, BehaviourStatueBlock.Behaviour behaviour) {
        DeferredBlock<BehaviourStatueBlock> block = BLOCKS.register(id, () -> new BehaviourStatueBlock(behaviour, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block, new Item.Properties());
        BEHAVIOUR_STATUES.add(block);
        return block;
    }
}
