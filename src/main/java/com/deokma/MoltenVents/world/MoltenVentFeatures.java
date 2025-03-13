package com.deokma.MoltenVents.world;

import com.deokma.MoltenVents.MoltenVentsBiomeModification;
import com.deokma.MoltenVents.MoltenVents;
import com.deokma.MoltenVents.core.MoltenVentsConfiguredData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MoltenVentFeatures {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Identifier, Feature<?>> FEATURES = new HashMap<>();
    private static final List<String> CONFIGURED_FEATURE_FILENAMES = new ArrayList<>();

    // Flag to track the first load
    private static boolean isFirstLoad = true;

    public static void init() {
        // Register core features
        registerFeatures();

        // Register data loader that first loads files and then calls BiomeModification
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier(MoltenVents.MOD_ID, "configured_features_preload");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        if (isFirstLoad) {
                            //MoltenVents.LOGGER.info("Performing first load of configuration file names...");

                            // Load the list of filenames before main parsing
                            CONFIGURED_FEATURE_FILENAMES.clear();
                            CONFIGURED_FEATURE_FILENAMES.addAll(loadValidFeatureFilenames(manager));

                            // Call BiomeModification after loading the list
                            MoltenVentsBiomeModification.registerBiomeModifications();

                            // Set flag to indicate first load is complete
                            isFirstLoad = false;

                            //MoltenVents.LOGGER.info("First load of configuration file names completed");
                        } else {
                            //MoltenVents.LOGGER.info("Skipping repeated loading of configuration file names");
                        }
                    }
                });

        // Now load and parse the actual features
        registerResourceReloadListeners();
    }

    private static void registerFeatures() {
        // Register the MoltenVent feature
        registerFeature(new Identifier(MoltenVents.MOD_ID, "molten_vent"),
                new MoltenVentFeature(MoltenVentConfiguration.CODEC));
    }

    private static <T extends Feature<?>> T registerFeature(Identifier id, T feature) {
        FEATURES.put(id, feature);
        return Registry.register(Registries.FEATURE, id, feature);
    }

    private static void registerResourceReloadListeners() {
        // Register a listener for configured features
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
                new MoltenVentsConfiguredData());
    }

    public static List<String> getConfiguredFeatureFilenames() {
        return CONFIGURED_FEATURE_FILENAMES;
    }

    private static List<String> loadValidFeatureFilenames(ResourceManager manager) {
        List<String> filenames = new ArrayList<>();
        String folderPath = "worldgen/configured_feature";

        Map<Identifier, Resource> resources = manager.findResources(folderPath,
                path -> path.getPath().endsWith(".json"));
        //MoltenVents.LOGGER.info("Found {} configuration files", resources.size());

        for (Identifier id : resources.keySet()) {
            Optional<JsonObject> jsonOpt = parseJson(manager, id);
            if (jsonOpt.isPresent() && "molten_vents:molten_vent".equals(jsonOpt.get().get("type").getAsString())) {
                String filename = id.getPath().substring(id.getPath()
                        .lastIndexOf('/') + 1, id.getPath().lastIndexOf('.'));
                filenames.add(filename);
                //MoltenVents.LOGGER.info("Added configuration file name: {}", filename);
            }
        }

        return filenames;
    }

    public static Optional<JsonObject> parseJson(ResourceManager manager, Identifier id) {
        try {
            for (Resource resource : manager.getAllResources(id)) {
                try (InputStream stream = resource.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                    JsonObject json = GSON.fromJson(reader, JsonObject.class);
                    if (json != null && json.has("type")) {
                        return Optional.of(json);
                    }
                }
            }
        } catch (Exception e) {
            MoltenVents.LOGGER.error("Error processing {}: {}", id, e.getMessage());
        }
        return Optional.empty();
    }
}
