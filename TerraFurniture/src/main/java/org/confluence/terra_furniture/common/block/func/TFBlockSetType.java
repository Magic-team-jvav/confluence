package org.confluence.terra_furniture.common.block.func;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.List;
import java.util.function.Supplier;

public final class TFBlockSetType {
    private final BlockSetType type;
    private final Supplier<List<TagKey<Block>>> tagKeys;
    private final ResourceLocation particle;

    public TFBlockSetType(BlockSetType type, Supplier<List<TagKey<Block>>> key) {
        this.type = type;
        this.tagKeys = key;
        this.particle = null;
    }

    public TFBlockSetType(BlockSetType type, Supplier<List<TagKey<Block>>> key, ResourceLocation particle) {
        this.type = type;
        this.tagKeys = key;
        this.particle = particle;
    }

    public TFBlockSetType(String name, Supplier<List<TagKey<Block>>> key) {
        this.type = BlockSetType.register(new BlockSetType(name));
        this.tagKeys = key;
        this.particle = null;
    }

    public TFBlockSetType(String name, Supplier<List<TagKey<Block>>> key, ResourceLocation particle) {
        this.type = BlockSetType.register(new BlockSetType(name));
        this.tagKeys = key;
        this.particle = particle;
    }

    public TFBlockSetType(
            String name,
            boolean canOpenByHand,
            boolean canOpenByWindCharge,
            boolean canButtonBeActivatedByArrows,
            BlockSetType.PressurePlateSensitivity pressurePlateSensitivity,
            SoundType soundType,
            SoundEvent doorClose,
            SoundEvent doorOpen,
            SoundEvent trapdoorClose,
            SoundEvent trapdoorOpen,
            SoundEvent pressurePlateClickOff,
            SoundEvent pressurePlateClickOn,
            SoundEvent buttonClickOff,
            SoundEvent buttonClickOn,
            Supplier<List<TagKey<Block>>> key
    ) {
        this.type = BlockSetType.register(new BlockSetType(
                name,
                canOpenByHand,
                canOpenByWindCharge,
                canButtonBeActivatedByArrows,
                pressurePlateSensitivity,
                soundType,
                doorClose,
                doorOpen,
                trapdoorClose,
                trapdoorOpen,
                pressurePlateClickOff,
                pressurePlateClickOn,
                buttonClickOff,
                buttonClickOn
        ));
        this.tagKeys = key;
        this.particle = null;
    }

    public TFBlockSetType(
            String name,
            boolean canOpenByHand,
            boolean canOpenByWindCharge,
            boolean canButtonBeActivatedByArrows,
            BlockSetType.PressurePlateSensitivity pressurePlateSensitivity,
            SoundType soundType,
            SoundEvent doorClose,
            SoundEvent doorOpen,
            SoundEvent trapdoorClose,
            SoundEvent trapdoorOpen,
            SoundEvent pressurePlateClickOff,
            SoundEvent pressurePlateClickOn,
            SoundEvent buttonClickOff,
            SoundEvent buttonClickOn,
            Supplier<List<TagKey<Block>>> key,
            ResourceLocation particle
    ) {
        this.type = BlockSetType.register(new BlockSetType(
                name,
                canOpenByHand,
                canOpenByWindCharge,
                canButtonBeActivatedByArrows,
                pressurePlateSensitivity,
                soundType,
                doorClose,
                doorOpen,
                trapdoorClose,
                trapdoorOpen,
                pressurePlateClickOff,
                pressurePlateClickOn,
                buttonClickOff,
                buttonClickOn
        ));
        this.tagKeys = key;
        this.particle = particle;
    }

    public BlockSetType getType() {
        return type;
    }

    public Supplier<List<TagKey<Block>>> getTagKeys() {
        return tagKeys;
    }

    public String name() {
        return type.name();
    }

    public ResourceLocation getParticle() {
        return particle;
    }
}
