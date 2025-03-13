package com.deokma.MoltenVents.core;

import com.deokma.MoltenVents.MoltenVents;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simibubi.create.Create.GSON;

public class MoltenVentsPlacedData implements SimpleSynchronousResourceReloadListener {
    public static final Map<Identifier, PlacedFeature> PLACED_FEATURES = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(MoltenVents.MOD_ID, "placed_features");
    }

    @Override
    public void reload(ResourceManager manager) {
        try {
            PLACED_FEATURES.clear();
            String folderPath = "worldgen/placed_feature";
            MoltenVents.LOGGER.info("Loading placed features from folder: {}", folderPath);

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
                                if (!json.has("feature")) {
                                    MoltenVents.LOGGER.warn("Missing 'feature' field in {}", id);
                                    continue;
                                }

                                String featureStr = json.get("feature").getAsString();
                                if (!featureStr.startsWith("molten_vents:")) {
                                    MoltenVents.LOGGER.debug("Skipping unnecessary file: {}", id);
                                    continue; // Skip unnecessary files
                                }

                                // Get the feature ID from the file path
                                String path = id.getPath();
                                String filename = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
                                Identifier placedFeatureId = new Identifier(MoltenVents.MOD_ID, filename);

                                MoltenVents.LOGGER.info("Registered PlacedFeature: {}", placedFeatureId);
                            }
                        }
                    }
                } catch (Exception e) {
                    MoltenVents.LOGGER.error("Error loading placed feature from {}: {}", id, e.getMessage());
                }
            }
        } catch (Exception e) {
            MoltenVents.LOGGER.error("General error while loading placed features: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
