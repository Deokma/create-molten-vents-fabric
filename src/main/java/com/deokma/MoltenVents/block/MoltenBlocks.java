package com.deokma.MoltenVents.block;

import com.deokma.MoltenVents.MoltenVents;
import com.deokma.MoltenVents.MoltenVentsJsonReader;
import com.deokma.MoltenVents.block.custom.ActiveMoltenBlock;
import com.deokma.MoltenVents.block.custom.DormantMoltenBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MoltenBlocks {
    private MoltenBlocks() {
    }

    public static final Block ASURINE = Registries.BLOCK.get(new Identifier("create", "asurine"));
    public static final Block VERIDIUM = Registries.BLOCK.get(new Identifier("create", "veridium"));
    public static final Block CRIMSITE = Registries.BLOCK.get(new Identifier("create", "crimsite"));
    public static final Block OCHRUM = Registries.BLOCK.get(new Identifier("create", "ochrum"));
    public static final Block SCORCHIA = Registries.BLOCK.get(new Identifier("create", "scorchia"));
    public static final Block SCORIA = Registries.BLOCK.get(new Identifier("create", "scoria"));

    public static final List<Map<Block, Block>> moltenBlocks = new ArrayList<>();

    public static final Map<Block, Block> ACTIVE_BLOCKS_MAP = new HashMap<>();


    public static List<Map<Block, Block>> getMoltenBlocks() {
        return new ArrayList<>(moltenBlocks);
    }

    public static void createCustomMoltenBlocks() {
        MoltenVents.LOGGER.info("Loading custom molten blocks...");
        for (String blockName : MoltenVentsJsonReader.customBlocks) {
            MoltenVents.LOGGER.info("Registering molten block: " + blockName);
            moltenBlocks.add(createMoltenBlocks(blockName));
        }
    }


    public static Map<Block, Block> createMoltenBlocks(String blockName) {
        Block dormantBlock = new DormantMoltenBlock(FabricBlockSettings.copyOf(Blocks.TUFF)
                .sounds(BlockSoundGroup.TUFF)
                .pistonBehavior(PistonBehavior.BLOCK));

        Block activeBlock = new ActiveMoltenBlock(FabricBlockSettings.copyOf(Blocks.TUFF)
                .strength(1200)
                .sounds(BlockSoundGroup.TUFF)
                .luminance((state) -> 15));

        Registry.register(Registries.BLOCK,
                new Identifier(MoltenVents.MOD_ID, "dormant_molten_" + blockName), dormantBlock);
        Registry.register(Registries.BLOCK,
                new Identifier(MoltenVents.MOD_ID, "active_molten_" + blockName), activeBlock);

        MoltenVents.LOGGER.info("Registered blocks: dormant_molten_{} and active_molten_{}", blockName, blockName);

        ACTIVE_BLOCKS_MAP.put(dormantBlock, activeBlock);

        return Map.of(dormantBlock, activeBlock);
    }

    public static void registerMoltenBlocks() {
        MoltenVents.LOGGER.info("Registering Block for " + MoltenVents.MOD_ID);
    }
}
