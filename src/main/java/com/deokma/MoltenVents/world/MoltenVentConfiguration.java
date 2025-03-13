package com.deokma.MoltenVents.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record MoltenVentConfiguration(BlockStateProvider decorativeBlock, BlockStateProvider outerBlock,
                                      BlockStateProvider innerBlock, BlockStateProvider liquidBlock, IntProvider depth,
                                      boolean underwater) implements FeatureConfig {
    public static final Codec<MoltenVentConfiguration> CODEC =
            RecordCodecBuilder.create((fields) -> {
                return fields.group(BlockStateProvider.TYPE_CODEC.fieldOf("decorativeBlock")
                                .forGetter((v) -> {
                                    return v.decorativeBlock;
                                }), BlockStateProvider.TYPE_CODEC.fieldOf("outerBlock")
                                .forGetter((v) -> {
                                    return v.outerBlock;
                                }),
                        BlockStateProvider.TYPE_CODEC.fieldOf("innerBlock")
                                .forGetter((v) -> {
                                    return v.innerBlock;
                                }),
                        BlockStateProvider.TYPE_CODEC.fieldOf("liquidBlock")
                                .forGetter((v) -> {
                                    return v.liquidBlock;
                                }),
                        IntProvider.createValidatingCodec(10, 255).fieldOf("depth")
                                .forGetter((v) -> {
                                    return v.depth;
                                }),
                        Codec.BOOL.fieldOf("underwater").orElse(false)
                                .forGetter((v) -> {
                                    return v.underwater;
                                })
                ).apply(fields, MoltenVentConfiguration::new);
            });

}
