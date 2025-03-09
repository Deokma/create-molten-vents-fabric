package com.deokma.MoltenVents.block.custom;

import com.deokma.MoltenVents.block.MoltenBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;
import java.util.Map;

public class DormantMoltenBlock extends Block {
    public DormantMoltenBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (!world.isClient) {
            List<Map<Block, Block>> moltenBlocks = MoltenBlocks.getMoltenBlocks();
            for (Map<Block, Block> moltenBlockMap : moltenBlocks) {
                Block activeBlock = moltenBlockMap.get(this);
                if (activeBlock != null) {
                    world.setBlockState(pos, activeBlock.getDefaultState(), Block.NOTIFY_ALL);
                    break;
                }
            }
        }
    }
}