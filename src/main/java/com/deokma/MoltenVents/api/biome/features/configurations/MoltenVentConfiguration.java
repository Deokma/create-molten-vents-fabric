package com.deokma.MoltenVents.api.biome.features.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class MoltenVentConfiguration implements FeatureConfig {
    public static final Codec<MoltenVentConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                BlockStateProvider.TYPE_CODEC.fieldOf("decorativeBlock").forGetter(MoltenVentConfiguration::getDecorativeBlock),
                BlockStateProvider.TYPE_CODEC.fieldOf("outerBlock").forGetter(MoltenVentConfiguration::getOuterBlock),
                BlockStateProvider.TYPE_CODEC.fieldOf("innerBlock").forGetter(MoltenVentConfiguration::getInnerBlock),
                BlockStateProvider.TYPE_CODEC.fieldOf("liquidBlock").forGetter(MoltenVentConfiguration::getLiquidBlock),
                IntProvider.createValidatingCodec(10, 255).fieldOf("depth").forGetter(MoltenVentConfiguration::getDepth),
                Codec.BOOL.optionalFieldOf("underwater", false).forGetter(MoltenVentConfiguration::isUnderwater)
        ).apply(instance, MoltenVentConfiguration::new);
    });

    public static final CodecHolder<MoltenVentConfiguration> CODEC_HOLDER = CodecHolder.of(CODEC);

    private final BlockStateProvider decorativeBlock;
    private final BlockStateProvider outerBlock;
    private final BlockStateProvider innerBlock;
    private final BlockStateProvider liquidBlock;
    private final IntProvider depth;
    private final boolean underwater;

    public MoltenVentConfiguration(BlockStateProvider decorativeBlock, BlockStateProvider outerBlock, BlockStateProvider innerBlock, BlockStateProvider liquidBlock, IntProvider depth, boolean underwater) {
        this.decorativeBlock = decorativeBlock;
        this.outerBlock = outerBlock;
        this.innerBlock = innerBlock;
        this.liquidBlock = liquidBlock;
        this.depth = depth;
        this.underwater = underwater;
    }

    public BlockStateProvider getDecorativeBlock() {
        return this.decorativeBlock;
    }

    public BlockStateProvider getOuterBlock() {
        return this.outerBlock;
    }

    public BlockStateProvider getInnerBlock() {
        return this.innerBlock;
    }

    public BlockStateProvider getLiquidBlock() {
        return this.liquidBlock;
    }

    public IntProvider getDepth() {
        return this.depth;
    }

    public boolean isUnderwater() {
        return this.underwater;
    }
}
