package org.confluence.mod.mixin.world.level.levelgen.structure.templatesystem;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.confluence.mod.common.attachment.ChunkBrushData;
import org.confluence.mod.common.attachment.ChunkDropletsData;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.mixed.IStructureTemplate$StructureBlockInfo;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.mod.network.s2c.DropletsSyncPacketS2C;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @Inject(method = "fillFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;addToLists(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V"))
    private void fillBrushData(CallbackInfo ci, @Local(argsOnly = true) Level level, @Local(ordinal = 4) BlockPos blockpos3, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo) {
        if (level instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = new ChunkPos(blockpos3);

            ChunkBrushData data = ChunkBrushData.of(serverLevel);
            BrushData brushData = data.getDataMap().get(chunkPos);
            if (brushData != null) {
                int[] colors = brushData.get(blockpos3);
                if (colors != null) {
                    IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo).confluence$colors(colors);
                }
            }

            ChunkDropletsData data1 = ChunkDropletsData.of(serverLevel);
            Map<BlockPos, ParticleOptions> map = data1.getDataMap().get(chunkPos);
            if (map != null) {
                ParticleOptions particle = map.get(blockpos3);
                if (particle != null) {
                    IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo).confluence$setDroplets(particle);
                }
            }
        }
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag;add(Ljava/lang/Object;)Z", ordinal = 0))
    private void saveBrushData(CallbackInfoReturnable<CompoundTag> cir, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo, @Local(ordinal = 1) CompoundTag compoundtag) {
        IStructureTemplate$StructureBlockInfo info = IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo);

        int[] colors = info.confluence$getColors();
        if (colors != null) {
            compoundtag.putIntArray("confluence:colors", colors);
        }

        ParticleOptions particle = info.confluence$getDroplets();
        if (particle != null) {
            MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                ParticleTypes.CODEC.encodeStart(server.registryAccess().createSerializationContext(NbtOps.INSTANCE), particle).ifSuccess(result -> compoundtag.put("confluence:droplets", result));
            }
        }
    }

    @Inject(method = "loadPalette", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;addToLists(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V"))
    private void loadBrushData(CallbackInfo ci, @Local(ordinal = 0) CompoundTag compoundtag, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo) {
        if (compoundtag.contains("confluence:colors", ListTag.TAG_INT_ARRAY)) {
            IStructureTemplate$StructureBlockInfo info = IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo);

            int[] colors = compoundtag.getIntArray("confluence:colors");
            info.confluence$colors(colors);

            Tag tag = compoundtag.get("confluence:droplets");
            if (tag != null) {
                MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    ParticleTypes.CODEC.parse(server.registryAccess().createSerializationContext(NbtOps.INSTANCE), tag).ifSuccess(info::confluence$setDroplets);
                }
            }
        }
    }

    @Inject(method = "processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", ordinal = 1))
    private static void processBrushData(CallbackInfoReturnable<List<StructureTemplate.StructureBlockInfo>> cir, @Local(ordinal = 0) StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo, @Local(ordinal = 1) StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo1) {
        IStructureTemplate$StructureBlockInfo from = IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo);
        IStructureTemplate$StructureBlockInfo to = IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo1);

        to.confluence$colors(from.confluence$getColors());
        to.confluence$setDroplets(from.confluence$getDroplets());
    }

    @Inject(method = "filterBlocks(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/world/level/block/Block;Z)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
    private void filterBrushData(CallbackInfoReturnable<ObjectArrayList<StructureTemplate.StructureBlockInfo>> cir, @Local ObjectArrayList<StructureTemplate.StructureBlockInfo> objectarraylist, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo) {
        IStructureTemplate$StructureBlockInfo from = IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo);
        IStructureTemplate$StructureBlockInfo to = IStructureTemplate$StructureBlockInfo.of(objectarraylist.getLast());

        to.confluence$colors(from.confluence$getColors());
        to.confluence$setDroplets(from.confluence$getDroplets());
    }

    @Inject(method = "placeInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Ljava/util/List;"))
    private void createBrushData(
            CallbackInfoReturnable<Boolean> cir,
            @Share("brushData") LocalRef<Map<ChunkPos, BrushData>> brushData,
            @Share("droplets") LocalRef<Map<ChunkPos, Map<BlockPos, ParticleOptions>>> droplets
    ) {
        brushData.set(new HashMap<>());
        droplets.set(new HashMap<>());
    }

    @Inject(method = "placeInWorld", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private void updateBrushData(
            CallbackInfoReturnable<Boolean> cir,
            @Local(argsOnly = true) StructurePlaceSettings settings,
            @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo,
            @Share("brushData") LocalRef<Map<ChunkPos, BrushData>> brushData,
            @Share("droplets") LocalRef<Map<ChunkPos, Map<BlockPos, ParticleOptions>>> droplets
    ) {
        IStructureTemplate$StructureBlockInfo info = IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo);

        int[] colors = info.confluence$getColors();
        if (colors != null) {
            BlockPos pos1 = structuretemplate$structureblockinfo.pos();
            colors = BrushData.rotateColor(colors, settings.getRotation());
            brushData.get().computeIfAbsent(new ChunkPos(pos1), pos3 -> new BrushData(new HashMap<>())).put(pos1, colors);
        }

        ParticleOptions particle = info.confluence$getDroplets();
        if (particle != null) {
            BlockPos pos1 = structuretemplate$structureblockinfo.pos();
            droplets.get().computeIfAbsent(new ChunkPos(pos1), pos3 -> new HashMap<>()).put(pos1, particle);
        }
    }

    @Inject(method = "placeInWorld", at = @At(value = "RETURN", ordinal = 1))
    private void placeBrushData(
            CallbackInfoReturnable<Boolean> cir,
            @Local(argsOnly = true) ServerLevelAccessor serverLevel,
            @Share("brushData") LocalRef<Map<ChunkPos, BrushData>> brushData,
            @Share("droplets") LocalRef<Map<ChunkPos, Map<BlockPos, ParticleOptions>>> droplets
    ) {
        for (Map.Entry<ChunkPos, BrushData> entry : brushData.get().entrySet()) {
            BrushingColorPacketS2C.sendToPlayersTrackingChunk(serverLevel.getLevel(), entry.getKey(), entry.getValue(), true);
        }

        for (Map.Entry<ChunkPos, Map<BlockPos, ParticleOptions>> entry : droplets.get().entrySet()) {
            ServerLevel level = serverLevel.getLevel();
            ChunkDropletsData data = ChunkDropletsData.of(level);
            ChunkPos chunkPos = entry.getKey();
            data.getDataMap().computeIfAbsent(chunkPos, pos1 -> new HashMap<>()).putAll(entry.getValue());
            for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(chunkPos, false)) {
                DropletsSyncPacketS2C.sendToClient(player, data.getDataMap(player, true));
            }
        }
    }

    @Mixin(StructureTemplate.StructureBlockInfo.class)
    public static abstract class StructureBlockInfoMixin implements IStructureTemplate$StructureBlockInfo {
        @Unique
        private int @Nullable [] confluence$colors;
        @Unique
        private @Nullable ParticleOptions confluence$droplets;

        @Override
        public void confluence$colors(int[] colors) {
            this.confluence$colors = colors;
        }

        @Override
        public int @Nullable [] confluence$getColors() {
            return confluence$colors;
        }

        @Override
        public void confluence$setDroplets(ParticleOptions particle) {
            this.confluence$droplets = particle;
        }

        @Override
        public @Nullable ParticleOptions confluence$getDroplets() {
            return confluence$droplets;
        }
    }
}
