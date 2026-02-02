package org.confluence.lib;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.lib.common.component.ToolMode;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.common.worldgen.structure.SimpleTemplatePiece;
import org.confluence.lib.util.DelayTaskHolder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

@Mod(ConfluenceMagicLib.LIB_ID)
public final class ConfluenceMagicLib {
    public static final String LIB_ID = "confluence_magic_lib";
    public static final String CONFLUENCE_ID = "confluence";
    public static final Logger LOGGER = LoggerFactory.getLogger("Confluence Magic Lib");
    public static final Supplier<Boolean> IS_CONFLUENCE_LOADED = Suppliers.memoize(() -> ModList.get().isLoaded(CONFLUENCE_ID));

    //region 数据附件
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPE = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, LIB_ID);

    public static final Supplier<AttachmentType<DelayTaskHolder>> DELAY_TASK_HOLDER = ATTACHMENT_TYPE.register("delay_task_holder", () -> AttachmentType.builder(DelayTaskHolder::new).build());
    //endregion

    // region 属性
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, LIB_ID);
    /// 玩家怪物生成速度系数
    public static final DeferredHolder<Attribute, RangedAttribute> MOB_SPAWN_SPEED_MULTIPLIER = registerRangedAttribute("player.mob_spawn_speed_multiplier", 1, 0, 1024, false, Attribute.Sentiment.NEUTRAL);
    /// 玩家怪物生成数量系数
    public static final DeferredHolder<Attribute, RangedAttribute> MOB_SPAWN_COUNT_MULTIPLIER = registerRangedAttribute("player.mob_spawn_count_multiplier", 1, 0, 1024, false, Attribute.Sentiment.NEUTRAL);

    private static DeferredHolder<Attribute, RangedAttribute> registerRangedAttribute(String name, double defaultValue, double min, double max, boolean syncable, Attribute.Sentiment sentiment) {
        return ATTRIBUTES.register(name, () -> {
            RangedAttribute attribute = new RangedAttribute("attribute.name." + name, defaultValue, min, max);
            attribute.setSyncable(syncable).setSentiment(sentiment);
            return attribute;
        });
    }
    // endregion

    // region 材料类型
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, LIB_ID);
    public static final Supplier<IngredientType<AmountIngredient>> AMOUNT_INGREDIENT_TYPE = INGREDIENT_TYPES.register("amount_ingredient", () -> new IngredientType<>(AmountIngredient.CODEC, AmountIngredient.STREAM_CODEC));
    // endregion

    // region 结构
    private static final DeferredRegister<StructurePieceType> PIECE_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PIECE, LIB_ID);
    public static final Supplier<StructurePieceType.StructureTemplateType> SIMPLE_TEMPLATE_PIECE = PIECE_TYPES.register("simple_template_piece", () -> SimpleTemplatePiece::new);
    public static final Supplier<StructurePieceType.ContextlessType> GRID_PIECE = PIECE_TYPES.register("grid_piece", () -> GridPiece::new);
    // endregion

    // region 数据组件
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LIB_ID);
    public static final Supplier<DataComponentType<ModRarity>> MOD_RARITY = DATA_COMPONENT_TYPES.registerComponentType("mod_rarity", builder -> builder.persistent(ModRarity.CODEC).networkSynchronized(ModRarity.STREAM_CODEC));
    public static final Supplier<DataComponentType<ToolMode>> TOOL_MODE = DATA_COMPONENT_TYPES.registerComponentType("tool_mode", builder -> builder.persistent(ToolMode.CODEC).networkSynchronized(ToolMode.STREAM_CODEC));
    public static final Supplier<DataComponentType<NbtComponent>> NBT = DATA_COMPONENT_TYPES.registerComponentType("nbt", builder -> builder.persistent(NbtComponent.CODEC).networkSynchronized(NbtComponent.STREAM_CODEC));
    // endregion

    // region 粒子
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, LIB_ID);
    public static final Supplier<ParticleType<CrossDustParticleOptions>> CROSS_DUST_PARTICLE = PARTICLES.register("cross_dust", () -> new ParticleType<>(false) {
        @Override
        @NotNull
        public MapCodec<CrossDustParticleOptions> codec() {
            return CrossDustParticleOptions.CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<? super RegistryFriendlyByteBuf, CrossDustParticleOptions> streamCodec() {
            return CrossDustParticleOptions.STREAM_CODEC;
        }
    });
    // endregion

    public ConfluenceMagicLib(IEventBus modEventBus, ModContainer modContainer) {
        StartupConfig.register(modContainer);
        ATTACHMENT_TYPE.register(modEventBus);
        ATTRIBUTES.register(modEventBus);
        INGREDIENT_TYPES.register(modEventBus);
        PIECE_TYPES.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);
        PARTICLES.register(modEventBus);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(LIB_ID, path);
    }
}
