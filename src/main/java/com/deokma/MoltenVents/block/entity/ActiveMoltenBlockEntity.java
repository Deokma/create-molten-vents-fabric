package com.deokma.MoltenVents.block.entity;

import com.deokma.MoltenVents.config.CommonConfig;
import com.deokma.MoltenVents.core.MoltenVentsConductiveData;
import com.deokma.MoltenVents.core.MoltenVentsConvertibleData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.deokma.MoltenVents.block.entity.MoltenBlockEntities.getMoltenBlockEntities;

public class ActiveMoltenBlockEntity extends BlockEntity {
    public ActiveMoltenBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(activeMoltenBlockEntity(blockState), blockPos, blockState);
    }

    private static final Map<Identifier, JsonElement> conductiveMap =
            MoltenVentsConductiveData.conductiveBlocksMap;
    private static final Map<Identifier, JsonElement> convertibleMap =
            MoltenVentsConvertibleData.convertibleBlocksMap;

    private static BlockEntityType<?> activeMoltenBlockEntity(BlockState blockState) {
        List<Map<Block, BlockEntityType<?>>> moltenBlockEntities = getMoltenBlockEntities();
        BlockEntityType<?> activeMoltenBlockEntity = null;
        for (int i = 0; i < moltenBlockEntities.size(); i++) {
            BlockEntityType<?> activeBlockEntity = moltenBlockEntities.get(i).get(blockState.getBlock());
            if (activeBlockEntity != null) {
                activeMoltenBlockEntity = activeBlockEntity;
            }
        }
        return (activeMoltenBlockEntity);
    }

    private static Map<Integer, List<Block>> getLists(BlockState blockState) {
        String name = Registries.BLOCK.getId(blockState.getBlock()).getPath().substring(14);

        List<Block> conductiveBlocks = new ArrayList<>(List.of());
        List<Block> convertibleBlocks = new ArrayList<>(List.of());
        JsonElement conductiveElement = conductiveMap.get(new Identifier("molten_vents",
                "molten_vents/blocks/conductive/" + name + ".json"));

        if (conductiveElement == null || !conductiveElement.isJsonObject()) {
            System.out.println("Error: missing data for block " + name);
            return Map.of(1, List.of(), 2, List.of());
        }

        JsonArray conductiveList = conductiveElement.getAsJsonObject().getAsJsonArray("values");

        for (int i = 0; i < conductiveList.size(); i++) {
            JsonElement blockName = conductiveList.get(i);
            conductiveBlocks.add(Registries.BLOCK.get(
                    new Identifier(blockName.toString().substring(1, blockName.toString().length() - 1))));
        }

        JsonElement convertibleElement = convertibleMap.get(new Identifier("molten_vents",
                "molten_vents/blocks/convertible/" + name + ".json"));

        if (convertibleElement == null || !convertibleElement.isJsonObject()) {
            System.out.println("Error: missing data for block " + name);
            return Map.of(1, List.of(), 2, List.of());
        }

        JsonArray convertibleList = convertibleElement.getAsJsonObject().getAsJsonArray("values");

        for (int i = 0; i < convertibleList.size(); i++) {
            JsonElement blockName = convertibleList.get(i);
            convertibleBlocks.add(Registries.BLOCK.get(
                    new Identifier(blockName.toString().substring(1, blockName.toString().length() - 1))));
        }
        return (Map.of(1, conductiveBlocks, 2, convertibleBlocks));
    }

    public static int spreadDistance = 5; //Max 5

    public static <T extends BlockEntity> void tick(World level, BlockPos blockPos, BlockState blockState, T t) {
        if (!level.isClient) {
            ServerWorld serverLevel = Objects.requireNonNull(level.getServer()).getWorld(level.getRegistryKey());
            Map<Integer, List<Block>> lists = getLists(blockState);
            List<Block> conductiveBlocks = lists.get(1);
            List<Block> convertibleBlocks = lists.get(2);
            spreadBlock(conductiveBlocks, convertibleBlocks, blockPos.up(), serverLevel);
            spreadBlock(conductiveBlocks, convertibleBlocks, blockPos.down(), serverLevel);
            spreadBlock(conductiveBlocks, convertibleBlocks, blockPos.north(), serverLevel);
            spreadBlock(conductiveBlocks, convertibleBlocks, blockPos.east(), serverLevel);
            spreadBlock(conductiveBlocks, convertibleBlocks, blockPos.south(), serverLevel);
            spreadBlock(conductiveBlocks, convertibleBlocks, blockPos.west(), serverLevel);
        }
    }

    private static void spreadBlock(List<Block> conductiveBlocks, List<Block> convertibleBlocks,
                                    BlockPos pos, ServerWorld serverWorld) {
        BlockState[] contacts =
                {serverWorld.getBlockState(pos.up()), serverWorld.getBlockState(pos.down()),
                        serverWorld.getBlockState(pos.north()),
                        serverWorld.getBlockState(pos.east()),
                        serverWorld.getBlockState(pos.south()),
                        serverWorld.getBlockState(pos.west())};
        Boolean isTouchingOrestone = false;
        for (int i = 0; i < contacts.length && !isTouchingOrestone; ++i) {
            //Check each non-diagonal neighbor to see if it is the same orestone
            if (conductiveBlocks.contains(contacts[i].getBlock())) {
                isTouchingOrestone = true;
            }
        }
        if (isTouchingOrestone && !serverWorld.getBlockState(pos).isLiquid() &&
                convertibleBlocks.contains(serverWorld.getBlockState(pos).getBlock()) ||
                isTouchingOrestone && !CommonConfig.get().useSource &&
                        convertibleBlocks.contains(serverWorld.getBlockState(pos).getBlock()) ||
                isTouchingOrestone && CommonConfig.get().useSource &&
                        convertibleBlocks.contains(serverWorld.getBlockState(pos).getBlock()) &&
                        serverWorld.getBlockState(pos).getFluidState().isStill() ||
                isTouchingOrestone && conductiveBlocks.contains(serverWorld.getBlockState(pos).getBlock())) {
            if (!serverWorld.getBlockState(pos).isLiquid() &&
                    convertibleBlocks.contains(serverWorld.getBlockState(pos).getBlock()) ||
                    !CommonConfig.get().useSource &&
                            convertibleBlocks.contains(serverWorld.getBlockState(pos).getBlock()) ||
                    CommonConfig.get().useSource &&
                            convertibleBlocks.contains(serverWorld.getBlockState(pos).getBlock()) &&
                            serverWorld.getBlockState(pos).getFluidState().isStill()) {
                serverWorld.setBlockState(pos, conductiveBlocks.get(0).getDefaultState(), Block.NOTIFY_ALL);
                serverWorld.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH,
                        SoundCategory.BLOCKS, 1.0f, 1.0f);
                serverWorld.getServer().getWorld(serverWorld.getRegistryKey()).spawnParticles(
                        ParticleTypes.LARGE_SMOKE,
                        (double) pos.getX() + 0.5D,
                        (double) pos.getY() + 0.25D,
                        (double) pos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
            }
            List<BlockPos> convertedBlocks = convertTouching(convertibleBlocks, conductiveBlocks, pos, serverWorld);
            if (convertedBlocks != null && !convertedBlocks.isEmpty() && spreadDistance >= 1) {
                List<BlockPos> secondaryConvertedBlocks = new ArrayList<>(List.of());
                for (int i = 0; i < convertedBlocks.size(); i++) {
                    List<BlockPos> minorConvertedBlocks =
                            convertTouching(convertibleBlocks, conductiveBlocks, convertedBlocks.get(i), serverWorld);
                    if (minorConvertedBlocks != null && minorConvertedBlocks.isEmpty()) {
                        secondaryConvertedBlocks.addAll(minorConvertedBlocks);
                    }
                }
                if (secondaryConvertedBlocks != null && !secondaryConvertedBlocks.isEmpty() && spreadDistance >= 2) {
                    List<BlockPos> teritaryConvertedBlocks = new ArrayList<>(List.of());
                    for (int i = 0; i < secondaryConvertedBlocks.size(); i++) {
                        List<BlockPos> minorConvertedBlocks =
                                convertTouching(convertibleBlocks, conductiveBlocks,
                                        secondaryConvertedBlocks.get(i), serverWorld);
                        if (minorConvertedBlocks != null && minorConvertedBlocks.isEmpty()) {
                            teritaryConvertedBlocks.addAll(minorConvertedBlocks);
                        }
                    }
                    if (teritaryConvertedBlocks != null && !teritaryConvertedBlocks.isEmpty() && spreadDistance >= 3) {
                        List<BlockPos> quaternaryConvertedBlocks = new ArrayList<>(List.of());
                        for (int i = 0; i < teritaryConvertedBlocks.size(); i++) {
                            List<BlockPos> minorConvertedBlocks =
                                    convertTouching(convertibleBlocks, conductiveBlocks,
                                            teritaryConvertedBlocks.get(i), serverWorld);
                            if (minorConvertedBlocks != null && minorConvertedBlocks.isEmpty()) {
                                quaternaryConvertedBlocks.addAll(minorConvertedBlocks);
                            }
                        }
                        if (quaternaryConvertedBlocks != null &&
                                !quaternaryConvertedBlocks.isEmpty()
                                && spreadDistance >= 4) {
                            List<BlockPos> quinaryConvertedBlocks = new ArrayList<>(List.of());
                            for (int i = 0; i < quaternaryConvertedBlocks.size(); i++) {
                                List<BlockPos> minorConvertedBlocks =
                                        convertTouching(convertibleBlocks, conductiveBlocks,
                                                quaternaryConvertedBlocks.get(i), serverWorld);
                                if (minorConvertedBlocks != null && minorConvertedBlocks.isEmpty()) {
                                    quinaryConvertedBlocks.addAll(minorConvertedBlocks);
                                }
                            }
                            if (quinaryConvertedBlocks != null &&
                                    !quinaryConvertedBlocks.isEmpty() && spreadDistance >= 5) {
                                List<BlockPos> senaryConvertedBlocks = new ArrayList<>(List.of());
                                for (int i = 0; i < quinaryConvertedBlocks.size(); i++) {
                                    List<BlockPos> minorConvertedBlocks =
                                            convertTouching(convertibleBlocks, conductiveBlocks,
                                                    quinaryConvertedBlocks.get(i), serverWorld);
                                    if (minorConvertedBlocks != null && minorConvertedBlocks.isEmpty()) {
                                        senaryConvertedBlocks.addAll(minorConvertedBlocks);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static List<BlockPos> convertTouching(List<Block> convertibleBlocks, List<Block> conductiveBlocks,
                                                  BlockPos pos, ServerWorld level) {
        List<BlockPos> lavaBlocks = getLavaTouching(convertibleBlocks, pos, level);
        List<BlockPos> touchingBlocks = getLavaTouching(conductiveBlocks, pos, level);
        if (lavaBlocks != null && !lavaBlocks.isEmpty()) {
            for (int i = 0; i < lavaBlocks.size(); ++i) {
                BlockPos lavaPos = lavaBlocks.get(i);
                if (!level.getBlockState(lavaPos).isLiquid() &&
                        convertibleBlocks.contains(level.getBlockState(lavaPos).getBlock()) ||
                        !CommonConfig.get().useSource &&
                                convertibleBlocks.contains(level.getBlockState(lavaPos).getBlock()) ||
                        CommonConfig.get().useSource &&
                                convertibleBlocks.contains(level.getBlockState(lavaPos).getBlock()) &&
                                level.getBlockState(lavaPos).getFluidState().isStill()) {
                    level.setBlockState(lavaPos, conductiveBlocks.get(0).getDefaultState(), Block.NOTIFY_ALL);
                    level.playSound(null, lavaPos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,
                            1.0f, 1.0f);
                    level.getServer().getWorld(level.getRegistryKey()).spawnParticles(
                            ParticleTypes.LARGE_SMOKE,
                            (double) lavaPos.getX() + 0.5D,
                            (double) lavaPos.getY() + 0.25D,
                            (double) lavaPos.getZ() + 0.5D,
                            8, 0.5D,
                            0.25D, 0.5D,
                            0.0D);
                }
            }
        }
        if (touchingBlocks.size() > 0) {
            return touchingBlocks;
        }
        return null;
    }

    private static List<BlockPos> getLavaTouching(List<Block> convertibleBlocks, BlockPos pos, World level) {
        BlockPos[] contacts = {pos.up(), pos.down(), pos.north(), pos.east(), pos.south(), pos.west()};
        List<BlockPos> lavaBlocks = new ArrayList<>(List.of());
        for (int i = 0; i < contacts.length; ++i) {
            //Check each non-diagonal neighbor to see if it is a corresponding orestone
            if (!level.getBlockState(contacts[i]).isLiquid() &&
                    convertibleBlocks.contains(
                            level.getBlockState(contacts[i]).getBlock()) ||
                    !CommonConfig.get().useSource &&
                            convertibleBlocks.contains(level.getBlockState(contacts[i]).getBlock()) ||
                    CommonConfig.get().useSource &&
                            convertibleBlocks.contains(level.getBlockState(contacts[i]).getBlock()) &&
                            level.getBlockState(contacts[i]).getFluidState().isStill() ||
                    convertibleBlocks.contains(level.getBlockState(contacts[i]).getBlock())) {
                lavaBlocks.add(contacts[i]);
            }
        }
        if (!lavaBlocks.isEmpty()) {
            return lavaBlocks;
        }
        return null;
    }
}