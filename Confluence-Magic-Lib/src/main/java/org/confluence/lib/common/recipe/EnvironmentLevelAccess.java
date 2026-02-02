package org.confluence.lib.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class EnvironmentLevelAccess implements ContainerLevelAccess {
    protected @Nullable Player player;
    protected @Nullable Level level;
    protected @Nullable BlockPos pos;

    public EnvironmentLevelAccess(@Nullable Level level, @Nullable BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public void initializeIfNeeded(Player player) {
        if (this.player == null) this.player = player;
        if (level == null) this.level = player.level();
        if (pos == null) {
            Vec3 start = player.getEyePosition(0.5F);
            Vec3 lookVector = player.getViewVector(0.5F);
            double range = Math.max(player.blockInteractionRange(), player.entityInteractionRange());
            Vec3 end = start.add(lookVector.x * range, lookVector.y * range, lookVector.z * range);
            ClipContext context = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.of(player));
            BlockHitResult blockResult = player.level().clip(context);
            if (blockResult.getType() == HitResult.Type.BLOCK) {
                this.pos = blockResult.getBlockPos();
            }
        }
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public @Nullable Level getLevel() {
        return level;
    }

    public @Nullable BlockPos getPos() {
        return pos;
    }

    @ApiStatus.OverrideOnly
    public <R extends Recipe<?>> boolean matches(R recipe) {
        return true;
    }

    public Optional<Holder<Biome>> getBiome() {
        return level == null || pos == null ? Optional.empty() : Optional.of(level.getBiome(pos));
    }

    public boolean isBiome(Function<Holder<Biome>, Boolean> predicate) {
        return getBiome().map(predicate).orElse(false);
    }

    public Iterable<BlockPos> searchBox(int inflate) {
        return level == null || pos == null ? List.of() : BlockPos.betweenClosed(pos.offset(-inflate, -inflate, -inflate), pos.offset(inflate, inflate, inflate));
    }

    public boolean anyMatch(Predicate<BlockState> predicate, int inflate) {
        if (level != null) {
            for (BlockPos blockPos : searchBox(inflate)) {
                if (predicate.test(level.getBlockState(blockPos))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> levelPosConsumer) {
        return level == null || pos == null ? Optional.empty() : Optional.of(levelPosConsumer.apply(level, pos));
    }

    public static EnvironmentLevelAccess empty() {
        return new EnvironmentLevelAccess(null, null);
    }

    public static Matcher matcher(@Nullable HolderSet<Biome> biome, @Nullable SearchContext block, boolean ectoMist) {
        return new Matcher(Optional.ofNullable(biome), Optional.ofNullable(block), ectoMist);
    }

    public record Matcher(
            Optional<HolderSet<Biome>> biome,
            Optional<SearchContext> block,
            boolean graveyard
    ) {
        public static final Matcher EMPTY = new Matcher(Optional.empty(), Optional.empty(), false);
        public static final Codec<Matcher> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.BIOME).lenientOptionalFieldOf("biome").forGetter(Matcher::biome),
                SearchContext.CODEC.lenientOptionalFieldOf("block").forGetter(Matcher::block),
                Codec.BOOL.lenientOptionalFieldOf("graveyard", false).forGetter(Matcher::graveyard)
        ).apply(instance, Matcher::new));
        public static final MapCodec<Matcher> MAP_CODEC = CODEC.lenientOptionalFieldOf("environment", EMPTY);
        public static final StreamCodec<RegistryFriendlyByteBuf, Matcher> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.BIOME)), Matcher::biome,
                ByteBufCodecs.optional(SearchContext.STREAM_CODEC), Matcher::block,
                ByteBufCodecs.BOOL, Matcher::graveyard,
                Matcher::new
        );

        public boolean matches(EnvironmentLevelAccess access) {
            Player player = access.getPlayer();
            Level level = access.getLevel();
            BlockPos pos = access.getPos();
            if (player == null || level == null || pos == null) return false;
            if (!matchesBiome(player, level, pos)) return false;
            if (!matchesBlock(player, level, pos)) return false;
            if (!matchesGraveyard(player, level, pos)) return false;
            return true;
        }

        public boolean matchesBiome(Player player, Level level, BlockPos pos) {
            return biome.isEmpty() || biome.get().contains(level.getBiome(pos));
        }

        public boolean matchesBlock(Player player, Level level, BlockPos pos) {
            return block.isEmpty() || block.get().matches(level, pos);
        }

        /// 灵雾环境
        public boolean matchesGraveyard(Player player, Level level, BlockPos pos) {
            return !graveyard || isGraveyard(player, level, pos);
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof Matcher(
                    Optional<HolderSet<Biome>> biome1,
                    Optional<SearchContext> block1,
                    boolean ectoMist1
            ) && graveyard == ectoMist1 && Objects.equals(block, block1) && Objects.equals(biome, biome1));
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(biome);
            result = 31 * result + Objects.hashCode(block);
            result = 31 * result + Boolean.hashCode(graveyard);
            return result;
        }

        private static boolean isGraveyard(Player player, Level level, BlockPos pos) {
            return true; // confluence mixin here
        }

        public List<Component> toDescriptions() {
            List<Component> list = new ArrayList<>();
            biome.ifPresent(biomes -> {
                list.add(Component.translatable("jei.tooltip.environment.biome").withStyle(ChatFormatting.AQUA));
                for (Holder<Biome> holder : biomes) {
                    list.add(Component.translatable(Util.makeDescriptionId("biome", holder.getKey().location())).withStyle(ChatFormatting.GRAY));
                }
            });
            block.ifPresent(context -> {
                list.add(Component.translatable("jei.tooltip.environment.block").withStyle(ChatFormatting.AQUA));
                list.addAll(context.toDescriptions());
            });
            if (graveyard) {
                list.add(Component.translatable("jei.tooltip.environment.graveyard").withStyle(ChatFormatting.AQUA));
            }
            return list;
        }
    }

    public record SearchContext(
            int inflate,
            Optional<HolderSet<Block>> blocks,
            List<StatePropertiesPredicate> statePredicates,
            Optional<HolderSet<Fluid>> fluids
    ) {
        public static final Codec<SearchContext> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("inflate").forGetter(SearchContext::inflate),
                RegistryCodecs.homogeneousList(Registries.BLOCK).lenientOptionalFieldOf("blocks").forGetter(SearchContext::blocks),
                StatePropertiesPredicate.CODEC.listOf().lenientOptionalFieldOf("state_predicates", List.of()).forGetter(SearchContext::statePredicates),
                RegistryCodecs.homogeneousList(Registries.FLUID).lenientOptionalFieldOf("fluids").forGetter(SearchContext::fluids)
        ).apply(instance, SearchContext::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SearchContext> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, SearchContext::inflate,
                ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.BLOCK)), SearchContext::blocks,
                StatePropertiesPredicate.STREAM_CODEC.apply(ByteBufCodecs.list()), SearchContext::statePredicates,
                ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.FLUID)), SearchContext::fluids,
                SearchContext::new
        );

        public boolean matches(Level level, BlockPos pos) {
            if (blocks.isPresent() || !statePredicates.isEmpty() || fluids.isPresent()) {
                for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-inflate, -inflate, -inflate), pos.offset(inflate, inflate, inflate))) {
                    BlockState blockState = level.getBlockState(blockPos);
                    if (blocks.isPresent() && blockState.is(blocks.get())) return true;
                    if (statePredicates.stream().anyMatch(predicates -> predicates.matches(blockState)))
                        return true;
                    if (fluids.isPresent() && blockState.getFluidState().is(fluids.get()))
                        return true;
                }
                return false;
            }
            return true;
        }

        public List<Component> toDescriptions() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("jei.tooltip.environment.block.inflate", inflate).withStyle(ChatFormatting.GRAY));
            blocks.ifPresent(blockz -> {
                list.add(Component.translatable("jei.tooltip.environment.block.blocks").withStyle(ChatFormatting.GRAY));
                blockz.stream().map(holder -> holder.value().getName().withStyle(ChatFormatting.DARK_GRAY)).forEach(list::add);
            });
            for (StatePropertiesPredicate predicate : statePredicates) {
                list.add(Component.translatable("jei.tooltip.environment.block.predicates").withStyle(ChatFormatting.GRAY));
                for (StatePropertiesPredicate.PropertyMatcher property : predicate.properties()) {
                    String s = property.name() + '=';
                    if (property.valueMatcher() instanceof StatePropertiesPredicate.ExactMatcher(
                            String value
                    )) {
                        s += value;
                    } else if (property.valueMatcher() instanceof StatePropertiesPredicate.RangedMatcher(
                            Optional<String> minValue, Optional<String> maxValue
                    )) {
                        if (minValue.isPresent()) {
                            if (maxValue.isPresent()) {
                                s += '[' + minValue.get() + ", " + maxValue.get() + ']';
                            } else {
                                s += '[' + minValue.get() + ",)";
                            }
                        } else if (maxValue.isPresent()) {
                            s += "(," + maxValue.get() + ']';
                        }
                    } else {
                        s += property.valueMatcher();
                    }
                    list.add(Component.translatable("jei.tooltip.environment.block.predicates.property", s).withStyle(ChatFormatting.DARK_GRAY));
                }
            }
            fluids.ifPresent(fluidz -> {
                list.add(Component.translatable("jei.tooltip.environment.block.fluids").withStyle(ChatFormatting.GRAY));
                fluidz.stream().map(holder -> holder.value().getFluidType()).distinct()
                        .forEach(type -> list.add(Component.translatable(type.getDescriptionId()).withStyle(ChatFormatting.DARK_GRAY)));
            });
            return list;
        }
    }
}
