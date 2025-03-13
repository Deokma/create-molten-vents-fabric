package com.deokma.MoltenVents;

import com.deokma.MoltenVents.block.MoltenBlocks;
import com.deokma.MoltenVents.block.entity.MoltenBlockEntities;
import com.deokma.MoltenVents.config.CommonConfig;
import com.deokma.MoltenVents.core.MoltenVentsConductiveData;
import com.deokma.MoltenVents.core.MoltenVentsConvertibleData;
import com.deokma.MoltenVents.item.MoltenItems;
import com.deokma.MoltenVents.world.MoltenVentFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoltenVents implements ModInitializer {
    public static final String MOD_ID = "molten_vents";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        LOGGER.info("Initializing " + MOD_ID);

        try {
            MoltenVentsJsonReader.main();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        MoltenBlocks.createCustomMoltenBlocks();
        MoltenBlocks.registerMoltenBlocks();

        MoltenBlockEntities.createCustomMoltenBlockEntities();
        MoltenBlockEntities.registerMoltenBlockEntities();

        MoltenItems.createCustomMoltenItems();
        MoltenItems.registerMoltenItems();

        CommonConfig.register();

        MoltenVentFeatures.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MoltenVentsConductiveData());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MoltenVentsConvertibleData());


        LOGGER.info(MOD_ID + " successfully initialized!");
    }
}