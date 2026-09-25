package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.TorchBlocks;
import org.mesdag.opallight.impl.OpalDataProvider;
import org.mesdag.portlib.registries.PortDeferredBlock;

import java.util.concurrent.CompletableFuture;

/// 彩色光定义，产物是 `assets/confluence/opal_data/confluence.json`。
///
/// 颜色取自泰拉瑞亚官方 wiki 的火把光色（RGB）。wiki 的通道值可以大于 1（表示增益），
/// 这里按最大通道归一化到 0~1：OpalLight 的 OpalColor 只接受 0~1，归一化保留色相，
/// 亮度由方块自身的光照等级决定。
public final class ModOpalDataProvider extends OpalDataProvider {
    public ModOpalDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Confluence.MODID);
    }

    @Override
    public void gather() {
        // 宝石火把
        addTorch(TorchBlocks.RED_TORCH, 0xFF1A1A);
        addTorch(TorchBlocks.ORANGE_TORCH, 0xFF8000);
        addTorch(TorchBlocks.YELLOW_TORCH, 0xFFFF00);
        addTorch(TorchBlocks.GREEN_TORCH, 0x00FF1A);
        addTorch(TorchBlocks.BLUE_TORCH, 0x0014FF);
        addTorch(TorchBlocks.WHITE_TORCH, 0xFFFFFF);
        addTorch(TorchBlocks.PURPLE_TORCH, 0xFF00FF);
        addTorch(TorchBlocks.PINK_TORCH, 0xFF00FF);

        // 材料与特殊火把
        addTorch(TorchBlocks.ICE_TORCH, 0xB2D9FF);
        addTorch(TorchBlocks.BONE_TORCH, 0xBA80FF);
        addTorch(TorchBlocks.ULTRABRIGHT_TORCH, 0x95FFEF);
        /// 恶魔火把在 wiki 上是两组颜色线性循环，这里取中间的 (0.75, 0.3, 0.75)。
        addTorch(TorchBlocks.DEMON_TORCH, 0xFF66FF);
        addTorch(TorchBlocks.CURSED_TORCH, 0xD9FFB2);
        addTorch(TorchBlocks.ICHOR_TORCH, 0xFFFFA3);
        /// 彩虹火把在 wiki 上是虹色循环，没有固定值；OpalLight 的定义是静态色，这里按白色处理。
        addTorch(TorchBlocks.RAINBOW_TORCH, 0xFFFFFF);

        // 生物群系火把
        addTorch(TorchBlocks.DESERT_TORCH, 0xFF9B64);
        addTorch(TorchBlocks.CORAL_TORCH, 0x31FF9D);
        addTorch(TorchBlocks.CORRUPT_TORCH, 0xAD49FF);
        addTorch(TorchBlocks.CRIMSON_TORCH, 0xFF805B);
        addTorch(TorchBlocks.HALLOWED_TORCH, 0xFF7AF5);
        addTorch(TorchBlocks.JUNGLE_TORCH, 0x84FF9E);
        addTorch(TorchBlocks.MUSHROOM_TORCH, 0x40A6FF);
        /// 以太火把在 wiki 上只标注了「紫色」，这里取它贴图火焰的紫色。
        addTorch(TorchBlocks.AETHER_TORCH, 0xAE68FF);
    }

    /// 墙挂形态与直立形态本就是同一根火把，注册名只差 `_torch` / `_wall_torch`，一并登记成同色。
    private void addTorch(PortDeferredBlock<?> torch, int color) {
        add(torch.get(), color);
        ResourceLocation wallId = Confluence.asResource(torch.getId().getPath().replace("_torch", "_wall_torch"));
        add(BuiltInRegistries.BLOCK.get(wallId), color);
    }
}
