package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
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
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.StatueBlock;
import org.confluence.mod.common.block.functional.BehaviourStatueBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.projectile.boulder.Boulder3x3Entity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.util.DateUtils;
import org.mesdag.portlib.registries.PortBlockRegistration;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class StatueBlocks {
    public static void init() {}

    public static final PortBlockRegistration BLOCKS = PortRegisterHandler.block(Confluence.MODID);
    private static List<PortDeferredBlock<BehaviourStatueBlock>> BEHAVIOUR_STATUES = new ArrayList<>();

    public static final PortDeferredBlock<StatueBlock> A_STATUE = registerWithEnglishName("a_statue", "A字雕像", "'A' Statue");
    public static final PortDeferredBlock<StatueBlock> B_STATUE = registerWithEnglishName("b_statue", "B字雕像", "'B' Statue");
    public static final PortDeferredBlock<StatueBlock> C_STATUE = registerWithEnglishName("c_statue", "C字雕像", "'C' Statue");
    public static final PortDeferredBlock<StatueBlock> D_STATUE = registerWithEnglishName("d_statue", "D字雕像", "'D' Statue");
    public static final PortDeferredBlock<StatueBlock> E_STATUE = registerWithEnglishName("e_statue", "E字雕像", "'E' Statue");
    public static final PortDeferredBlock<StatueBlock> F_STATUE = registerWithEnglishName("f_statue", "F字雕像", "'F' Statue");
    public static final PortDeferredBlock<StatueBlock> G_STATUE = registerWithEnglishName("g_statue", "G字雕像", "'G' Statue");
    public static final PortDeferredBlock<StatueBlock> H_STATUE = registerWithEnglishName("h_statue", "H字雕像", "'H' Statue");
    public static final PortDeferredBlock<StatueBlock> I_STATUE = registerWithEnglishName("i_statue", "I字雕像", "'I' Statue");
    public static final PortDeferredBlock<StatueBlock> J_STATUE = registerWithEnglishName("j_statue", "J字雕像", "'J' Statue");
    public static final PortDeferredBlock<StatueBlock> K_STATUE = registerWithEnglishName("k_statue", "K字雕像", "'K' Statue");
    public static final PortDeferredBlock<StatueBlock> L_STATUE = registerWithEnglishName("l_statue", "L字雕像", "'L' Statue");
    public static final PortDeferredBlock<StatueBlock> M_STATUE = registerWithEnglishName("m_statue", "M字雕像", "'M' Statue");
    public static final PortDeferredBlock<StatueBlock> N_STATUE = registerWithEnglishName("n_statue", "N字雕像", "'N' Statue");
    public static final PortDeferredBlock<StatueBlock> O_STATUE = registerWithEnglishName("o_statue", "O字雕像", "'O' Statue");
    public static final PortDeferredBlock<StatueBlock> P_STATUE = registerWithEnglishName("p_statue", "P字雕像", "'P' Statue");
    public static final PortDeferredBlock<StatueBlock> Q_STATUE = registerWithEnglishName("q_statue", "Q字雕像", "'Q' Statue");
    public static final PortDeferredBlock<StatueBlock> R_STATUE = registerWithEnglishName("r_statue", "R字雕像", "'R' Statue");
    public static final PortDeferredBlock<StatueBlock> S_STATUE = registerWithEnglishName("s_statue", "S字雕像", "'S' Statue");
    public static final PortDeferredBlock<StatueBlock> T_STATUE = registerWithEnglishName("t_statue", "T字雕像", "'T' Statue");
    public static final PortDeferredBlock<StatueBlock> U_STATUE = registerWithEnglishName("u_statue", "U字雕像", "'U' Statue");
    public static final PortDeferredBlock<StatueBlock> V_STATUE = registerWithEnglishName("v_statue", "V字雕像", "'V' Statue");
    public static final PortDeferredBlock<StatueBlock> W_STATUE = registerWithEnglishName("w_statue", "W字雕像", "'W' Statue");
    public static final PortDeferredBlock<StatueBlock> X_STATUE = registerWithEnglishName("x_statue", "X字雕像", "'X' Statue");
    public static final PortDeferredBlock<StatueBlock> Y_STATUE = registerWithEnglishName("y_statue", "Y字雕像", "'Y' Statue");
    public static final PortDeferredBlock<StatueBlock> Z_STATUE = registerWithEnglishName("z_statue", "Z字雕像", "'Z' Statue");
    public static final PortDeferredBlock<StatueBlock> N0_STATUE = registerWithEnglishName("n0_statue", "0字雕像", "'0' Statue");
    public static final PortDeferredBlock<StatueBlock> N1_STATUE = registerWithEnglishName("n1_statue", "1字雕像", "'1' Statue");
    public static final PortDeferredBlock<StatueBlock> N2_STATUE = registerWithEnglishName("n2_statue", "2字雕像", "'2' Statue");
    public static final PortDeferredBlock<StatueBlock> N3_STATUE = registerWithEnglishName("n3_statue", "3字雕像", "'3' Statue");
    public static final PortDeferredBlock<StatueBlock> N4_STATUE = registerWithEnglishName("n4_statue", "4字雕像", "'4' Statue");
    public static final PortDeferredBlock<StatueBlock> N5_STATUE = registerWithEnglishName("n5_statue", "5字雕像", "'5' Statue");
    public static final PortDeferredBlock<StatueBlock> N6_STATUE = registerWithEnglishName("n6_statue", "6字雕像", "'6' Statue");
    public static final PortDeferredBlock<StatueBlock> N7_STATUE = registerWithEnglishName("n7_statue", "7字雕像", "'7' Statue");
    public static final PortDeferredBlock<StatueBlock> N8_STATUE = registerWithEnglishName("n8_statue", "8字雕像", "'8' Statue");
    public static final PortDeferredBlock<StatueBlock> N9_STATUE = registerWithEnglishName("n9_statue", "9字雕像", "'9' Statue");

    public static final PortDeferredBlock<StatueBlock> PERIOD_STATUE = register("period_statue", "句号雕像");
    public static final PortDeferredBlock<StatueBlock> EXCLAMATION_MARK_STATUE = register("exclamation_mark_statue", "叹号雕像");
    public static final PortDeferredBlock<StatueBlock> QUESTION_MARK_STATUE = register("question_mark_statue", "问号雕像");

    public static final PortDeferredBlock<StatueBlock> ANVIL_STATUE = register("anvil_statue", "砧雕像");
    public static final PortDeferredBlock<StatueBlock> ARMOR_STATUE = register("armor_statue", "盔甲雕像");
    public static final PortDeferredBlock<StatueBlock> AXE_STATUE = register("axe_statue", "战斧雕像");
    public static final PortDeferredBlock<StatueBlock> BOOMERANG_STATUE = register("boomerang_statue", "回旋镖雕像");
    public static final PortDeferredBlock<StatueBlock> BOOT_STATUE = register("boot_statue", "靴子雕像");
    public static final PortDeferredBlock<StatueBlock> BOW_STATUE = register("bow_statue", "弓雕像");
    public static final PortDeferredBlock<StatueBlock> CROSS_STATUE = register("cross_statue", "十字架雕像");
    public static final PortDeferredBlock<StatueBlock> GARGOYLE_STATUE = register("gargoyle_statue", "石像鬼雕像");
    public static final PortDeferredBlock<StatueBlock> GLOOM_STATUE = register("gloom_statue", "幽冥雕像");
    public static final PortDeferredBlock<StatueBlock> HAMMER_STATUE = register("hammer_statue", "战锤雕像");
    public static final PortDeferredBlock<StatueBlock> PICKAXE_STATUE = register("pickaxe_statue", "镐雕像");
    public static final PortDeferredBlock<StatueBlock> PILLAR_STATUE = register("pillar_statue", "石柱雕像");
    public static final PortDeferredBlock<StatueBlock> POT_STATUE = register("pot_statue", "陶罐雕像");
    public static final PortDeferredBlock<StatueBlock> POTION_STATUE = register("potion_statue", "药水雕像");
    public static final PortDeferredBlock<StatueBlock> REAPER_STATUE = register("reaper_statue", "死神雕像");
    public static final PortDeferredBlock<StatueBlock> SHIELD_STATUE = register("shield_statue", "护盾雕像");
    public static final PortDeferredBlock<StatueBlock> SPEAR_STATUE = register("spear_statue", "长矛雕像");
    public static final PortDeferredBlock<StatueBlock> SUNFLOWER_STATUE = register("sunflower_statue", "向日葵雕像");
    public static final PortDeferredBlock<StatueBlock> SWORD_STATUE = register("sword_statue", "宝剑雕像");
    public static final PortDeferredBlock<StatueBlock> TREE_STATUE = register("tree_statue", "树木雕像");
    public static final PortDeferredBlock<StatueBlock> WOMEN_STATUE = register("women_statue", "女人雕像");
    public static final PortDeferredBlock<StatueBlock> LIHZAHRD_STATUE = register("lihzahrd_statue", "丛林蜥蜴雕像");
    public static final PortDeferredBlock<StatueBlock> LIHZAHRD_GUARDIAN_STATUE = register("lihzahrd_guardian_statue", "丛林蜥蜴守卫雕像");
    public static final PortDeferredBlock<StatueBlock> LIHZAHRD_WATCHER_STATUE = register("lihzahrd_watcher_statue", "丛林蜥蜴看守人雕像");

    public static final PortDeferredBlock<BehaviourStatueBlock> ARMED_ZOMBIE_STATUE = registerBehaviour("armed_zombie_statue", "武装僵尸雕像", new BehaviourStatueBlock.SummonBehaviour<>(true, false, (state, level, pos) -> {
        Zombie zombie = new Zombie(level);
        zombie.setPos(pos);
        zombie.setItemInHand(InteractionHand.MAIN_HAND, SwordItems.ZOMBIE_ARM.toStack());
        return zombie;
    }));
    // Bat Statue
    // Blood Zombie Statue
    public static final PortDeferredBlock<BehaviourStatueBlock> BONE_SKELETON_STATUE = registerSimpleSummon("bone_skeleton_statue", "骨头骷髅雕像", true, EntityType.SKELETON::create);
    // Chest Statue
    public static final PortDeferredBlock<BehaviourStatueBlock> CORRUPT_STATUE = registerSimpleSummon("corrupt_statue", "腐化雕像", true, level -> MonsterEntities.EATER_OF_SOULS.get().create(level));
    // Crab Statue
    public static final PortDeferredBlock<BehaviourStatueBlock> DRIPPLER_STATUE = registerSimpleSummon("drippler_statue", "滴滴怪雕像", true, level -> MonsterEntities.DRIPPLER.get().create(level));
    public static final PortDeferredBlock<BehaviourStatueBlock> EYEBALL_STATUE = registerSimpleSummon("eyeball_statue", "魔眼雕像", true, level -> MonsterEntities.DEMON_EYE.get().create(level));
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
    public static final PortDeferredBlock<BehaviourStatueBlock> SKELETON_STATUE = registerBehaviour("skeleton_statue", "骷髅雕像", new BehaviourStatueBlock.SummonBehaviour<>(true, true, (state, level, pos) -> {
        Skeleton skeleton = new Skeleton(EntityType.SKELETON, level);
        skeleton.setPos(pos);
        return skeleton;
    }, entity -> entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)));
    public static final PortDeferredBlock<BehaviourStatueBlock> SLIME_STATUE = registerSimpleSummon("slime_statue", "史莱姆雕像", false, level -> MonsterEntities.BLUE_SLIME.get().create(level));
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
    public static final PortDeferredBlock<BehaviourStatueBlock> BOMB_STATUE = registerBehaviour("bomb_statue", "炸弹雕像", new BehaviourStatueBlock.SummonBehaviour<>(true, false, 200, (state, level, pos) -> {
        ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, ConsumableItems.BOMB.toStack());
        itemEntity.setPickUpDelay(0);
        return itemEntity;
    }));
    public static final PortDeferredBlock<BehaviourStatueBlock> HEART_STATUE = registerBehaviour("heart_statue", "心形雕像", new BehaviourStatueBlock.SummonBehaviour<>(true, false, 200, (state, level, pos) -> {
        ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, DateUtils.getHeartItem().getDefaultInstance());
        itemEntity.setPickUpDelay(0);
        return itemEntity;
    }));
    public static final PortDeferredBlock<BehaviourStatueBlock> STAR_STATUE = registerBehaviour("star_statue", "星星雕像", new BehaviourStatueBlock.SummonBehaviour<>(true, false, 200, (state, level, pos) -> {
        ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, DateUtils.getStarItem().getDefaultInstance());
        itemEntity.setPickUpDelay(0);
        return itemEntity;
    }));
    // Mushroom Statue
    // Boulder Statue
    public static final PortDeferredBlock<BehaviourStatueBlock> BOULDER_3X_STATUE = registerBehaviourWithEnglishName("boulder_3x_statue", "三倍巨石雕像", "Boulder 3x Statue", new BehaviourStatueBlock.SummonBehaviour<>(false, true, 200, (state, level, pos) -> {
        Direction facing = state.getValue(StatueBlock.FACING);
        Boulder3x3Entity entity = new Boulder3x3Entity(level, pos.relative(facing, 1).add(0, Mth.EPSILON - 0.5, 0), FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState());
        entity.shoot(facing, 0.4);
        return entity;
    }));
    public static final PortDeferredBlock<BehaviourStatueBlock> BAST_STATUE = registerBehaviour("bast_statue", "巴斯特雕像", new BehaviourStatueBlock.Behaviour() {
        @Override
        public void entityTick(Level level, BlockPos pos, BlockState blockState, BehaviourStatueBlock.BEntity entity) {
            if (!level.isClientSide && level.getGameTime() % 400 == 0 && blockState.getValue(StateProperties.DRIVE)) {
                Vec3 center = pos.getCenter();
                for (Player player : level.players()) {
                    if (player.distanceToSqr(center) < 1024.0 && !player.hasEffect(ModEffects.THE_BAST_DEFENSE.get())) {
                        player.addEffect(new MobEffectInstance(ModEffects.THE_BAST_DEFENSE.get(), 420));
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
        public BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
            return state.setValue(StateProperties.DRIVE, true);
        }
    });
    // Angel Statue

    public static final Supplier<BlockEntityType<BehaviourStatueBlock.BEntity>> BLOCK_ENTITY = BLOCK_ENTITIES.register("behaviour_statue_entity", () -> {
        BehaviourStatueBlock[] validBlocks = BEHAVIOUR_STATUES.stream().map(Supplier::get).toArray(BehaviourStatueBlock[]::new);
        BEHAVIOUR_STATUES = null;
        return BlockEntityType.Builder.of(BehaviourStatueBlock.BEntity::new, validBlocks).build(DSL.remainderType());
    });

    private static PortDeferredBlock<StatueBlock> register(String id) {
        PortDeferredBlock<StatueBlock> block = BLOCKS.register(id, () -> new StatueBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
        ModItems.BLOCK_ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static PortDeferredBlock<BehaviourStatueBlock> registerBehaviour(String id, BehaviourStatueBlock.Behaviour behaviour) {
        PortDeferredBlock<BehaviourStatueBlock> block = BLOCKS.register(id, () -> new BehaviourStatueBlock(behaviour, BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        BEHAVIOUR_STATUES.add(block);
        return block;
    }

    private static PortDeferredBlock<BehaviourStatueBlock> registerSimpleSummon(String id, boolean noDrops, Function<Level, Entity> factory) {
        PortDeferredBlock<BehaviourStatueBlock> block = BLOCKS.register(id, () -> new BehaviourStatueBlock(new BehaviourStatueBlock.SummonBehaviour<>(true, noDrops, (state, level, pos) -> {
            Entity entity = factory.apply(level);
            entity.setPos(pos);
            return entity;
        }), BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        BEHAVIOUR_STATUES.add(block);
        return block;
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag) {
        BLOCKS.getEntries().forEach(block -> tag.add(block.get()));
    }

    private static PortDeferredBlock<StatueBlock> register(String registryId, String chineseName) {
        PortDeferredBlock<StatueBlock> entry = register(registryId);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredBlock<BehaviourStatueBlock> registerBehaviour(String registryId, String chineseName, BehaviourStatueBlock.Behaviour behaviour) {
        PortDeferredBlock<BehaviourStatueBlock> entry = registerBehaviour(registryId, behaviour);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredBlock<BehaviourStatueBlock> registerSimpleSummon(String registryId, String chineseName, boolean noDrops, Function<Level, Entity> factory) {
        PortDeferredBlock<BehaviourStatueBlock> entry = registerSimpleSummon(registryId, noDrops, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredBlock<StatueBlock> registerWithEnglishName(String registryId, String chineseName, String englishName) {
        PortDeferredBlock<StatueBlock> entry = register(registryId);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static PortDeferredBlock<BehaviourStatueBlock> registerBehaviourWithEnglishName(String registryId, String chineseName, String englishName, BehaviourStatueBlock.Behaviour behaviour) {
        PortDeferredBlock<BehaviourStatueBlock> entry = registerBehaviour(registryId, behaviour);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
