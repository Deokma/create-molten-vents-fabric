package com.deokma.MoltenVents.core;

import com.deokma.MoltenVents.MoltenVents;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simibubi.create.Create.GSON;

public class MoltenVentsConfiguredData implements SimpleSynchronousResourceReloadListener {
    public static final Map<Identifier, ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(MoltenVents.MOD_ID, "configured_features");
    }

    @Override
    public void reload(ResourceManager manager) {
        try {
            CONFIGURED_FEATURES.clear();
            String folderPath = "worldgen/configured_feature";
            MoltenVents.LOGGER.info("Loading configured features from folder: {}", folderPath);

            for (Identifier id : manager.findResources(folderPath,
                    path -> path.getPath().endsWith(".json")).keySet()) {
                try {
                    List<Resource> resources = manager.getAllResources(id);
                    for (Resource resource : resources) {
                        try (InputStream stream = resource.getInputStream();
                             BufferedReader reader = new BufferedReader(
                                     new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                            JsonObject json = GSON.fromJson(reader, JsonObject.class);
                            if (json != null) {
                                // Get the feature type from the JSON
                                if (!json.has("type") || !json.get("type")
                                        .getAsString().equals("molten_vents:molten_vent")) {
                                    // Skipping unnecessary files
                                    MoltenVents.LOGGER.debug("Skipping file with type {}: {}", json.get("type").getAsString(), id);
                                    continue;
                                }

                                // Get the feature ID from the file path
                                String path = id.getPath();
                                String filename = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
                                Identifier configuredFeatureId = new Identifier(MoltenVents.MOD_ID, filename);

                                MoltenVents.LOGGER.info("Registered ConfiguredFeature: {}", configuredFeatureId);
                            }
                        }
                    }
                } catch (Exception e) {
                    MoltenVents.LOGGER.error("Error loading configured feature from {}: {}", id, e.getMessage());
                }
            }
        } catch (Exception e) {
            MoltenVents.LOGGER.error("General error while loading configured features: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
