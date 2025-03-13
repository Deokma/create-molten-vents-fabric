package com.deokma.MoltenVents.world;

import com.mojang.serialization.Codec;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import static net.minecraft.block.Block.NOTIFY_ALL;


public class MoltenVentFeature extends Feature<MoltenVentConfiguration> {
    public MoltenVentFeature(Codec<MoltenVentConfiguration> codec) {
        super(codec);
    }


    @Override
    public boolean generate(FeatureContext<MoltenVentConfiguration> context) {
        MoltenVentConfiguration config = context.getConfig();
        Random random = context.getRandom();
        BlockPos origin = context.getOrigin();
        StructureWorldAccess level = context.getWorld();

        BlockStateProvider decorativeBlock = config.decorativeBlock();
        BlockStateProvider outerBlock = config.outerBlock();
        BlockStateProvider innerBlock = config.innerBlock();
        BlockStateProvider liquidBlock = config.liquidBlock();
        boolean underwater = config.underwater();
        int depth = config.depth().get(random);

        if (depth > 80 && underwater) {
            depth = 80;
        }

        boolean suitableEnvironment = underwater
                ? level.getBlockState(origin.up(2)).isOf(Blocks.WATER)
                : !level.getBlockState(origin.down()).isOf(Blocks.WATER);

        if (suitableEnvironment && level.getBlockState(origin.down()).isSolid()) {
            placePartialDiagonal(level, random, origin.up(), outerBlock, 3, 0.8, decorativeBlock);
            placeDiagonal(level, random, origin.up(), outerBlock, 3, 3, 0.66, decorativeBlock);
            placePartialDiagonal(level, random, origin, outerBlock, 3, 0.7, decorativeBlock);
            placeStraight(level, random, origin, outerBlock, 3, 3, 1, decorativeBlock);
            placeDiagonal(level, random, origin, outerBlock, 0, 3, 1.6, null);
            placeStraight(level, random, origin.down(), outerBlock, 3, 3, 1, null);
            placeDiagonal(level, random, origin.down(), outerBlock, 0, 3, 1.6, null);

            if (depth > 10) {
                for (int y = 1; y <= (depth / 2) + 1; y++) {
                    placeStraight(level, random, origin.down(y), outerBlock,
                            0, y <= (depth / 2) + 1 ? 2 : 1, y <= (depth / 2) + 1 ? 0.8 : 0.33, null);
                    placeDiagonal(level, random, origin.down(y), outerBlock,
                            0, y <= (depth / 2) + 1 ? 2 : 1, y <= (depth / 2) + 1 ? 0.75 : 0.275, null);
                    placeBlock(level, random, origin.down(y), liquidBlock, 1, null);
                }
            }
            if (underwater) {
                level.setBlockState(origin, Blocks.WATER.getDefaultState(), NOTIFY_ALL);
            } else {
                level.setBlockState(origin, Blocks.AIR.getDefaultState(), NOTIFY_ALL);
            }
            level.setBlockState(origin.down(), innerBlock.get(random, origin.down()), NOTIFY_ALL);
            return true;
        } else {
            return false;
        }
    }

    private void placeStraight(StructureWorldAccess level, Random random, BlockPos pos,
                               BlockStateProvider block, int min, int max, double prob,
                               @Nullable BlockStateProvider deco) {
        for (int r = min - 1; r < max; r++) {
            placeBlock(level, random, pos.north(r), block, prob, deco);
            placeBlock(level, random, pos.east(r), block, prob, deco);
            placeBlock(level, random, pos.south(r), block, prob, deco);
            placeBlock(level, random, pos.west(r), block, prob, deco);
        }
    }

    private void placeDiagonal(StructureWorldAccess level, Random random, BlockPos pos,
                               BlockStateProvider block, int min, int max, double prob,
                               @Nullable BlockStateProvider deco) {
        for (int r = min - 1; r < max; r++) {
            placeBlock(level, random, pos.north(r).east(), block, prob, deco);
            placeBlock(level, random, pos.north(r).west(), block, prob, deco);
            placeBlock(level, random, pos.south(r).west(), block, prob, deco);
            placeBlock(level, random, pos.south(r).east(), block, prob, deco);
        }
    }

    private void placePartialDiagonal(StructureWorldAccess level, Random random, BlockPos pos,
                                      BlockStateProvider block, int radius, double prob,
                                      @Nullable BlockStateProvider deco) {
        placeBlock(level, random, pos.north(radius).east(), block, prob, deco);
        placeBlock(level, random, pos.north(radius).west(), block, prob, deco);
        placeBlock(level, random, pos.south(radius).west(), block, prob, deco);
        placeBlock(level, random, pos.south(radius).east(), block, prob, deco);
    }

    private void placeBlock(StructureWorldAccess level, Random random, BlockPos pos,
                            BlockStateProvider block, double prob, @Nullable BlockStateProvider deco) {
        if (random.nextDouble() < prob) {
            level.setBlockState(pos, block.get(random, pos), 3);
            if (deco != null && random.nextDouble() < prob / 3) {
                BlockPos offset = randomOffset(pos);
                level.setBlockState(offset, deco.get(random, offset), 3);
                if (level.getBlockState(offset.down()).isAir()) {
                    level.setBlockState(offset.down(), block.get(random, offset.down()), 3);
                }
            }
        }
    }

    private BlockPos randomOffset(BlockPos pos) {
        switch (Random.create().nextInt(8)) {
            case 0:
                return pos.up();
            case 1:
                return pos.north();
            case 2:
                return pos.east();
            case 3:
                return pos.south();
            case 4:
                return pos.west();
            case 5:
                return pos.north().east();
            case 6:
                return pos.north().west();
            case 7:
                return pos.south().east();
            default:
                return pos.south().west();
        }
    }
}
