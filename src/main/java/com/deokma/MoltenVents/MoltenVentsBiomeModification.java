package com.deokma.MoltenVents;

import com.deokma.MoltenVents.world.MoltenVentFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MoltenVentsBiomeModification {

    public static void registerBiomeModifications() {
        List<String> registeredFeatures = new ArrayList<>();

        Predicate<BiomeSelectionContext> oceanSelector = context ->
                context.getBiomeKey().equals(BiomeKeys.OCEAN) ||
                        context.getBiomeKey().equals(BiomeKeys.DEEP_OCEAN) ||
                        context.getBiomeKey().equals(BiomeKeys.WARM_OCEAN) ||
                        context.getBiomeKey().equals(BiomeKeys.LUKEWARM_OCEAN) ||
                        context.getBiomeKey().equals(BiomeKeys.COLD_OCEAN) ||
                        context.getBiomeKey().equals(BiomeKeys.DEEP_LUKEWARM_OCEAN) ||
                        context.getBiomeKey().equals(BiomeKeys.DEEP_COLD_OCEAN) ||
                        context.getBiomeKey().equals(BiomeKeys.DEEP_FROZEN_OCEAN);

        for (String filename : MoltenVentFeatures.getConfiguredFeatureFilenames()) {
            Identifier placedFeatureId = new Identifier(MoltenVents.MOD_ID, filename);
            registerFeatureToBiomes(oceanSelector, GenerationStep.Feature.UNDERGROUND_DECORATION, placedFeatureId);
            registeredFeatures.add(placedFeatureId.toString());
            System.out.println("Registered feature: " + placedFeatureId);
        }

        System.out.println("Total registered features: " + registeredFeatures.size());
        registeredFeatures.forEach(feature -> System.out.println("- " + feature));
    }

    private static void registerFeatureToBiomes(Predicate<BiomeSelectionContext> biomeSelector,
                                                GenerationStep.Feature step,
                                                Identifier featureId) {
        BiomeModifications.addFeature(
                biomeSelector,
                step,
                RegistryKey.of(net.minecraft.registry.RegistryKeys.PLACED_FEATURE, featureId)
        );
    }
}
