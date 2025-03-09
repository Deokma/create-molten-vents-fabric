package com.deokma.MoltenVents.block.entity;

import com.deokma.MoltenVents.MoltenVents;
import com.deokma.MoltenVents.block.MoltenBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoltenBlockEntities {
    public static final List<Map<Block, BlockEntityType<?>>> moltenBlockEntities = new ArrayList<>(List.of());

    public static List<Map<Block, BlockEntityType<?>>> getMoltenBlockEntities() {
        return new ArrayList<>(moltenBlockEntities);
    }

    public static void createCustomMoltenBlockEntities() {
        for (Map<Block, Block> moltenBlock : MoltenBlocks.moltenBlocks) {
            moltenBlockEntities.add(createMoltenBlockEntities(moltenBlock.keySet().iterator().next()));
        }
    }

    public static Map<Block, BlockEntityType<?>> createMoltenBlockEntities(Block block) {
        Block activeMoltenBlock = null;

        for (var moltenBlockMap : MoltenBlocks.moltenBlocks) {
            Block activeBlock = moltenBlockMap.get(block);
            if (activeBlock != null) {
                activeMoltenBlock = activeBlock;
            }
        }

        if (activeMoltenBlock == null) {
            return Map.of(); // Вернуть пустую мапу, если не найден активный блок
        }

        BlockEntityType<ActiveMoltenBlockEntity> blockEntityType = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("molten_vents", activeMoltenBlock.getTranslationKey() + "_block_entity"),
                FabricBlockEntityTypeBuilder.create(ActiveMoltenBlockEntity::new, activeMoltenBlock).build()
        );

        return Map.of(activeMoltenBlock, blockEntityType);
    }

    public static void registerMoltenBlockEntities() {
        MoltenVents.LOGGER.info("Registering Block Entities for " + MoltenVents.MOD_ID);
    }
}
