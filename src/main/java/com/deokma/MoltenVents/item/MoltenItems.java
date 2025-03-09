package com.deokma.MoltenVents.item;

import com.deokma.MoltenVents.MoltenVents;
import com.deokma.MoltenVents.block.MoltenBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoltenItems {
    public static final List<ItemStack> moltenItems = new ArrayList<>();

    public static final ItemGroup MOLTEN_VENTS_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(MoltenVents.MOD_ID, "molten_vents"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("item_group.molten_vents"))
                    .icon(() -> new ItemStack(Registries.ITEM.get(
                            new Identifier(MoltenVents.MOD_ID, "active_molten_veridium"))))
                    .entries((displayContext, entries) -> {
                        entries.addAll(moltenItems);
                    })
                    .build());

    public static void createCustomMoltenItems() {
        for (Map<Block, Block> blockPair : MoltenBlocks.moltenBlocks) {
            Block dormantBlock = blockPair.keySet().iterator().next();
            Block activeBlock = blockPair.values().iterator().next();

            Item dormantItem = new BlockItem(dormantBlock, new FabricItemSettings());
            Item activeItem = new BlockItem(activeBlock, new FabricItemSettings());

            String dormantBlockName = Registries.BLOCK.getId(dormantBlock).getPath();
            String activeBlockName = Registries.BLOCK.getId(activeBlock).getPath();

            // Регистрация предметов
            Registry.register(Registries.ITEM, new Identifier(MoltenVents.MOD_ID, dormantBlockName), dormantItem);
            Registry.register(Registries.ITEM, new Identifier(MoltenVents.MOD_ID, activeBlockName), activeItem);

            moltenItems.add(dormantItem.getDefaultStack());
            moltenItems.add(activeItem.getDefaultStack());
        }
    }


    public static void registerMoltenItems() {
        MoltenVents.LOGGER.info("Registering Items for " + MoltenVents.MOD_ID);
    }
}
