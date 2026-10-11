package org.confluence.mod.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/// 1.21.1 Only
public class ConfluenceMixinPlugin implements IMixinConfigPlugin {
    private boolean immersivePortals;

    @Override
    public void onLoad(String mixinPackage) {
        this.immersivePortals = LoadingModList.get().getModFileById("immersive_portals_core") != null;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!immersivePortals) return true;
        return !mixinClassName.equals("org.confluence.mod.mixin.world.level.chunk.ChunkMapMixin$TrackedEntityMixin");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
