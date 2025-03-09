package com.deokma.MoltenVents.block.custom;

import com.deokma.MoltenVents.block.entity.ActiveMoltenBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.deokma.MoltenVents.block.entity.MoltenBlockEntities.getMoltenBlockEntities;

public class ActiveMoltenBlock extends BlockWithEntity implements BlockEntityProvider {
    public ActiveMoltenBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState blockState,
                                                                  BlockEntityType<T> blockEntityType) {
        List<Map<Block, BlockEntityType<?>>> moltenBlockEntities = getMoltenBlockEntities();
        BlockEntityType<?> activeMoltenBlockEntity = null;
        for (int i = 0; i < moltenBlockEntities.size(); i++) {
            BlockEntityType<?> activeBlockEntity = moltenBlockEntities.get(i).get(this.asBlock());
            if (activeBlockEntity != null) {
                activeMoltenBlockEntity = activeBlockEntity;
            }
        }
        return blockEntityType == activeMoltenBlockEntity ? ActiveMoltenBlockEntity::tick : null;
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        List<Map<Block, BlockEntityType<?>>> moltenBlockEntities = getMoltenBlockEntities();
        BlockEntityType<?> activeMoltenBlockEntity = null;
        for (int i = 0; i < moltenBlockEntities.size(); i++) {
            BlockEntityType<?> activeBlockEntity = moltenBlockEntities.get(i).get(this.asBlock());
            if (activeBlockEntity != null) {
                activeMoltenBlockEntity = activeBlockEntity;
            }
        }
        return activeMoltenBlockEntity.instantiate(blockPos, blockState);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
