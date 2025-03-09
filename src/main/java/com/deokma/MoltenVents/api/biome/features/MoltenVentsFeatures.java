package com.deokma.MoltenVents.api.biome.features;

import com.deokma.MoltenVents.api.biome.features.configurations.MoltenVentConfiguration;
import com.deokma.MoltenVents.api.biome.features.types.MoltenVentFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

import static com.deokma.MoltenVents.MoltenVents.MOD_ID;

public class MoltenVentsFeatures {

    public static final Feature<MoltenVentConfiguration> MOLTEN_VENT = Registry.register(
            Registries.FEATURE, // Используем Registry.FEATURE
            new Identifier(MOD_ID, "molten_vent"),
            new MoltenVentFeature(MoltenVentConfiguration.CODEC)
    );

    public static void register() {
        // Метод остаётся пустым, так как Registry.register уже выполняет всю работу.
    }
}
