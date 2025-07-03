package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.StatueBlock;
import org.confluence.mod.common.block.functional.BehaviourStatueBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.projectile.boulder.Boulder3x3Entity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.util.DateUtils;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import org.confluence.terraentity.entity.monster.prefab.FlyMonsterPrefab;
import org.confluence.terraentity.entity.monster.slime.BaseSlime;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StatueBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    static List<Supplier<BehaviourStatueBlock>> BEHAVIOUR_STATUES = new ArrayList<>();

    public static final DeferredBlock<StatueBlock> A_STATUE = register("a_statue");
    public static final DeferredBlock<StatueBlock> B_STATUE = register("b_statue");
    public static final DeferredBlock<StatueBlock> C_STATUE = register("c_statue");
    public static final DeferredBlock<StatueBlock> D_STATUE = register("d_statue");
    public static final DeferredBlock<StatueBlock> E_STATUE = register("e_statue");
    public static final DeferredBlock<StatueBlock> F_STATUE = register("f_statue");
    public static final DeferredBlock<StatueBlock> G_STATUE = register("g_statue");
    public static final DeferredBlock<StatueBlock> H_STATUE = register("h_statue");
    public static final DeferredBlock<StatueBlock> I_STATUE = register("i_statue");
    public static final DeferredBlock<StatueBlock> J_STATUE = register("j_statue");
    public static final DeferredBlock<StatueBlock> K_STATUE = register("k_statue");
    public static final DeferredBlock<StatueBlock> L_STATUE = register("l_statue");
    public static final DeferredBlock<StatueBlock> M_STATUE = register("m_statue");
    public static final DeferredBlock<StatueBlock> N_STATUE = register("n_statue");
    public static final DeferredBlock<StatueBlock> O_STATUE = register("o_statue");
    public static final DeferredBlock<StatueBlock> P_STATUE = register("p_statue");
    public static final DeferredBlock<StatueBlock> Q_STATUE = register("q_statue");
    public static final DeferredBlock<StatueBlock> R_STATUE = register("r_statue");
    public static final DeferredBlock<StatueBlock> S_STATUE = register("s_statue");
    public static final DeferredBlock<StatueBlock> T_STATUE = register("t_statue");
    public static final DeferredBlock<StatueBlock> U_STATUE = register("u_statue");
    public static final DeferredBlock<StatueBlock> V_STATUE = register("v_statue");
    public static final DeferredBlock<StatueBlock> W_STATUE = register("w_statue");
    public static final DeferredBlock<StatueBlock> X_STATUE = register("x_statue");
    public static final DeferredBlock<StatueBlock> Y_STATUE = register("y_statue");
    public static final DeferredBlock<StatueBlock> Z_STATUE = register("z_statue");
    public static final DeferredBlock<StatueBlock> N0_STATUE = register("n0_statue");
    public static final DeferredBlock<StatueBlock> N1_STATUE = register("n1_statue");
    public static final DeferredBlock<StatueBlock> N2_STATUE = register("n2_statue");
    public static final DeferredBlock<StatueBlock> N3_STATUE = register("n3_statue");
    public static final DeferredBlock<StatueBlock> N4_STATUE = register("n4_statue");
    public static final DeferredBlock<StatueBlock> N5_STATUE = register("n5_statue");
    public static final DeferredBlock<StatueBlock> N6_STATUE = register("n6_statue");
    public static final DeferredBlock<StatueBlock> N7_STATUE = register("n7_statue");
    public static final DeferredBlock<StatueBlock> N8_STATUE = register("n8_statue");
    public static final DeferredBlock<StatueBlock> N9_STATUE = register("n9_statue");

    public static final DeferredBlock<StatueBlock> PERIOD_STATUE = register("period_statue");
    public static final DeferredBlock<StatueBlock> EXCLAMATION_MARK_STATUE = register("exclamation_mark_statue");
    public static final DeferredBlock<StatueBlock> QUESTION_MARK_STATUE = register("question_mark_statue");


    public static final DeferredBlock<StatueBlock> ANVIL_STATUE = register("anvil_statue");
    public static final DeferredBlock<StatueBlock> ARMOR_STATUE = register("armor_statue");
    public static final DeferredBlock<StatueBlock> AXE_STATUE = register("axe_statue");
    public static final DeferredBlock<StatueBlock> BOOMERANG_STATUE = register("boomerang_statue");
    public static final DeferredBlock<StatueBlock> BOOT_STATUE = register("boot_statue");
    public static final DeferredBlock<StatueBlock> BOW_STATUE = register("bow_statue");
    public static final DeferredBlock<StatueBlock> CROSS_STATUE = register("cross_statue");
    public static final DeferredBlock<StatueBlock> GARGOYLE_STATUE = register("gargoyle_statue");
    public static final DeferredBlock<StatueBlock> GLOOM_STATUE = register("gloom_statue");
    public static final DeferredBlock<StatueBlock> HAMMER_STATUE = register("hammer_statue");
    public static final DeferredBlock<StatueBlock> PICKAXE_STATUE = register("pickaxe_statue");
    public static final DeferredBlock<StatueBlock> PILLAR_STATUE = register("pillar_statue");
    public static final DeferredBlock<StatueBlock> POT_STATUE = register("pot_statue");
    public static final DeferredBlock<StatueBlock> POTION_STATUE = register("potion_statue");
    public static final DeferredBlock<StatueBlock> REAPER_STATUE = register("reaper_statue");
    public static final DeferredBlock<StatueBlock> SHIELD_STATUE = register("shield_statue");
    public static final DeferredBlock<StatueBlock> SPEAR_STATUE = register("spear_statue");
    public static final DeferredBlock<StatueBlock> SUNFLOWER_STATUE = register("sunflower_statue");
    public static final DeferredBlock<StatueBlock> SWORD_STATUE = register("sword_statue");
    public static final DeferredBlock<StatueBlock> TREE_STATUE = register("tree_statue");
    public static final DeferredBlock<StatueBlock> WOMEN_STATUE = register("women_statue");
    public static final DeferredBlock<StatueBlock> LIHZAHRD_STATUE = register("lihzahrd_statue");
    public static final DeferredBlock<StatueBlock> LIHZAHRD_GUARDIAN_STATUE = register("lihzahrd_guardian_statue");
    public static final DeferredBlock<StatueBlock> LIHZAHRD_WATCHER_STATUE = register("lihzahrd_watcher_statue");


    public static final DeferredBlock<BehaviourStatueBlock> ARMED_ZOMBIE_STATUE = registerBehaviour("armed_zombie_statue", new BehaviourStatueBlock.SummonBehaviour<>(false, (state, level, pos) -> {
        Zombie zombie = new Zombie(level);
        zombie.setPos(pos);
        zombie.setItemInHand(InteractionHand.MAIN_HAND, SwordItems.ZOMBIE_ARM.toStack());
        return zombie;
    }));
    // Bat Statue
    // Blood Zombie Statue
    public static final DeferredBlock<BehaviourStatueBlock> BONE_SKELETON_STATUE = registerSimpleSummon("bone_skeleton_statue", true, level -> new Skeleton(EntityType.SKELETON, level));
    // Chest Statue
    public static final DeferredBlock<BehaviourStatueBlock> CORRUPT_STATUE = registerSimpleSummon("corrupt_statue", true, level -> new AbstractMonster(TEMonsterEntities.EATER_OF_SOULS.get(), level, FlyMonsterPrefab.EATER_OF_SOULS_BUILDER.get()));
    // Crab Statue
    public static final DeferredBlock<BehaviourStatueBlock> DRIPPLER_STATUE = registerSimpleSummon("drippler_statue", true, level -> new AbstractMonster(TEMonsterEntities.DRIPPLER.get(), level, FlyMonsterPrefab.DRIPPLER_BUILDER.get()));
    public static final DeferredBlock<BehaviourStatueBlock> EYEBALL_STATUE = registerSimpleSummon("eyeball_statue", true, level -> new DemonEye(TEMonsterEntities.DEMON_EYE.get(), level));
    // Goblin Statue
    // Granite Golem Statue
    // Harpy Statue
    // Hoplite Statue
    // Hornet Statue
    // Imp Statue
    // Jellyfish Statue
    // Medusa Statue
    // Pigron Statue
    // Piranha Statue
    // Shark Statue
    public static final DeferredBlock<BehaviourStatueBlock> SKELETON_STATUE = registerBehaviour("skeleton_statue", new BehaviourStatueBlock.SummonBehaviour<>(true, (state, level, pos) -> {
        Skeleton skeleton = new Skeleton(EntityType.SKELETON, level);
        skeleton.setPos(pos);
        return skeleton;
    }, entity -> entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)));
    public static final DeferredBlock<BehaviourStatueBlock> SLIME_STATUE = registerSimpleSummon("slime_statue", false, level -> new BaseSlime(TEMonsterEntities.BLUE_SLIME.get(), level, 0x73BCF4, 2));
    // Undead Viking Statue
    // Unicorn Statue
    // Wall Creeper Statue
    // Wraith Statue


    // Bird Statue
    // Buggy Statue
    // Bunny Statue
    // Butterfly Statue
    // Cockatiel Statue
    // Dragonfly Statue
    // Duck Statue
    // Firefly Statue
    // Fish Statue
    // Frog Statue
    // Grasshopper Statue
    // Macaw Statue
    // Mouse Statue
    // Owl Statue
    // Penguin Statue
    // Scorpion Statue
    // Seagull Statue
    // Snail Statue
    // Squirrel Statue
    // Toucan Statue
    // Turtle Statue
    // Worm Statue


    // King Statue
    // Queen Statue
    public static final DeferredBlock<BehaviourStatueBlock> BOMB_STATUE = registerBehaviour("bomb_statue", new BehaviourStatueBlock.SummonBehaviour<>(false, 200, (state, level, pos) -> {
        ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, ConsumableItems.BOMB.get().getDefaultInstance());
        itemEntity.setPickUpDelay(0);
        return itemEntity;
    }));
    public static final DeferredBlock<BehaviourStatueBlock> HEART_STATUE = registerBehaviour("heart_statue", new BehaviourStatueBlock.SummonBehaviour<>(false, 200, (state, level, pos) -> {
        ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, DateUtils.getHeartItem().getDefaultInstance());
        itemEntity.setPickUpDelay(0);
        return itemEntity;
    }));
    public static final DeferredBlock<BehaviourStatueBlock> STAR_STATUE = registerBehaviour("star_statue", new BehaviourStatueBlock.SummonBehaviour<>(false, 200, (state, level, pos) -> {
        ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, DateUtils.getStarItem().getDefaultInstance());
        itemEntity.setPickUpDelay(0);
        return itemEntity;
    }));
    // Mushroom Statue
    // Boulder Statue
    public static final DeferredBlock<BehaviourStatueBlock> BOULDER_3X_STATUE = registerBehaviour("boulder_3x_statue", new BehaviourStatueBlock.SummonBehaviour<>(true, 200, (state, level, pos) -> {
        Direction facing = state.getValue(StatueBlock.FACING);
        Boulder3x3Entity entity = new Boulder3x3Entity(level, pos.relative(facing, 1).add(0, Mth.EPSILON - 0.5, 0), FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState());
        entity.shoot(facing, 0.3);
        return entity;
    }));
    public static final DeferredBlock<BehaviourStatueBlock> BAST_STATUE = registerBehaviour("bast_statue", new BehaviourStatueBlock.Behaviour() {
        @Override
        public void entityTick(Level level, BlockPos pos, BlockState blockState, BehaviourStatueBlock.Entity entity) {
            if (!level.isClientSide && level.getGameTime() % 400 == 0 && blockState.getValue(StateProperties.DRIVE)) {
                Vec3 center = pos.getCenter();
                for (Player player : level.players()) {
                    if (player.distanceToSqr(center) < 1024.0 && !player.hasEffect(ModEffects.THE_BAST_DEFENSE)) {
                        player.addEffect(new MobEffectInstance(ModEffects.THE_BAST_DEFENSE, 420));
                    }
                }
            }
        }

        @Override
        public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
            if (state.getValue(StateProperties.VERTICAL_TWO_PART).isBase()) {
                level.setBlockAndUpdate(pos, state.cycle(StateProperties.DRIVE));
            }
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext pContext, BlockState original) {
            return original.setValue(StateProperties.DRIVE, true);
        }
    });
    // Angel Statue

    public static final Supplier<BlockEntityType<BehaviourStatueBlock.Entity>> BLOCK_ENTITY = BLOCK_ENTITIES.register("behaviour_statue_entity", () -> {
        BehaviourStatueBlock[] validBlocks = BEHAVIOUR_STATUES.stream().map(Supplier::get).toArray(BehaviourStatueBlock[]::new);
        BEHAVIOUR_STATUES = null;
        return BlockEntityType.Builder.of(BehaviourStatueBlock.Entity::new, validBlocks).build(DSL.remainderType());
    });

    private static DeferredBlock<StatueBlock> register(String id) {
        DeferredBlock<StatueBlock> block = BLOCKS.register(id, () -> new StatueBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
        ModItems.BLOCK_ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredBlock<BehaviourStatueBlock> registerBehaviour(String id, BehaviourStatueBlock.Behaviour behaviour) {
        DeferredBlock<BehaviourStatueBlock> block = BLOCKS.register(id, () -> new BehaviourStatueBlock(behaviour, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block, new Item.Properties());
        BEHAVIOUR_STATUES.add(block);
        return block;
    }

    private static DeferredBlock<BehaviourStatueBlock> registerSimpleSummon(String id, boolean noDrops, Function<Level, Entity> factory) {
        DeferredBlock<BehaviourStatueBlock> block = BLOCKS.register(id, () -> new BehaviourStatueBlock(new BehaviourStatueBlock.SummonBehaviour<>(noDrops, (state, level, pos) -> {
            Entity entity = factory.apply(level);
            entity.setPos(pos);
            return entity;
        }), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block, new Item.Properties());
        BEHAVIOUR_STATUES.add(block);
        return block;
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag) {
        BLOCKS.getEntries().forEach(block -> tag.add(block.get()));
    }
}
