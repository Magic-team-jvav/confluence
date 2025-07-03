package org.confluence.mod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.confluence.mod.common.attachment.ChunkBrushData;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.mixed.IStructureTemplate$StructureBlockInfo;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @Inject(method = "fillFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;addToLists(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V"))
    private void fillBrushData(Level level, BlockPos pos, Vec3i size, boolean withEntities, Block toIgnore, CallbackInfo ci, @Local(ordinal = 4) BlockPos blockpos3, @Local(ordinal = 5) BlockPos blockpos4, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo) {
        if (level instanceof ServerLevel serverLevel) {
            ChunkBrushData data = serverLevel.getData(ModAttachmentTypes.CHUNK_BRUSH_DATA);
            BrushData brushData = data.getDataMap().get(new ChunkPos(blockpos3));
            if (brushData != null) {
                int[] colors = brushData.get(blockpos3);
                if (colors != null) {
                    IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo).confluence$setColors(colors);
                }
            }
        }
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag;add(Ljava/lang/Object;)Z", ordinal = 0))
    private void saveBrushData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo, @Local(ordinal = 1) CompoundTag compoundtag) {
        int[] colors = IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo).confluence$getColors();
        if (colors != null) {
            compoundtag.putIntArray("confluence:colors", colors);
        }
    }

    @Inject(method = "loadPalette", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;addToLists(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V"))
    private void loadBrushData(HolderGetter<Block> blockGetter, ListTag paletteTag, ListTag blocksTag, CallbackInfo ci, @Local(ordinal = 0) CompoundTag compoundtag, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo) {
        if (compoundtag.contains("confluence:colors", ListTag.TAG_INT_ARRAY)) {
            int[] colors = compoundtag.getIntArray("confluence:colors");
            IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo).confluence$setColors(colors);
        }
    }

    @Inject(method = "processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", ordinal = 1))
    private static void processBrushData(ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, List<StructureTemplate.StructureBlockInfo> blockInfos, StructureTemplate template, CallbackInfoReturnable<List<StructureTemplate.StructureBlockInfo>> cir, @Local(ordinal = 0) StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo, @Local(ordinal = 1) StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo1) {
        IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo1).confluence$setColors(
                IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo).confluence$getColors()
        );
    }

    @Inject(method = "filterBlocks(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/world/level/block/Block;Z)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
    private void filterBrushData(BlockPos pos, StructurePlaceSettings settings, Block block, boolean relativePosition, CallbackInfoReturnable<ObjectArrayList<StructureTemplate.StructureBlockInfo>> cir, @Local ObjectArrayList<StructureTemplate.StructureBlockInfo> objectarraylist, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo) {
        IStructureTemplate$StructureBlockInfo.of(objectarraylist.getLast()).confluence$setColors(
                IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo).confluence$getColors()
        );
    }

    @Inject(method = "placeInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Ljava/util/List;"))
    private void createBrushData(ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, RandomSource random, int flags, CallbackInfoReturnable<Boolean> cir, @Share("brushData") LocalRef<Map<ChunkPos, BrushData>> brushData) {
        brushData.set(new Hashtable<>());
    }

    @Inject(method = "placeInWorld", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private void updateBrushData(ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, RandomSource random, int flags, CallbackInfoReturnable<Boolean> cir, @Local StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo, @Share("brushData") LocalRef<Map<ChunkPos, BrushData>> brushData) {
        int[] colors = IStructureTemplate$StructureBlockInfo.of(structuretemplate$structureblockinfo).confluence$getColors();
        if (colors != null) {
            BlockPos pos1 = structuretemplate$structureblockinfo.pos();
            colors = BrushData.rotateColor(colors, settings.getRotation());
            brushData.get().computeIfAbsent(new ChunkPos(pos1), pos3 -> new BrushData(new Hashtable<>())).put(pos1, colors);
        }
    }

    @Inject(method = "placeInWorld", at = @At(value = "RETURN", ordinal = 1))
    private void placeBrushData(ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, RandomSource random, int flags, CallbackInfoReturnable<Boolean> cir, @Share("brushData") LocalRef<Map<ChunkPos, BrushData>> brushData) {
        for (Map.Entry<ChunkPos, BrushData> entry : brushData.get().entrySet()) {
            BrushingColorPacketS2C.sendToPlayersTrackingChunk(serverLevel.getLevel(), entry.getKey(), entry.getValue(), true);
        }
    }

    @Mixin(StructureTemplate.StructureBlockInfo.class)
    public static abstract class StructureBlockInfoMixin implements IStructureTemplate$StructureBlockInfo {
        @Unique
        private int @Nullable [] confluence$colors;

        @Override
        public void confluence$setColors(int[] colors) {
            this.confluence$colors = colors;
        }

        @Override
        public int @Nullable [] confluence$getColors() {
            return confluence$colors;
        }
    }
}
