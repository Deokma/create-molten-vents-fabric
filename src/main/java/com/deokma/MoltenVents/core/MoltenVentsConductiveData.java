package com.deokma.MoltenVents.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoltenVentsConductiveData implements SimpleSynchronousResourceReloadListener {
    public static Map<Identifier, JsonElement> conductiveBlocksMap = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("molten_vents", "conductive_data");
    }

    @Override
    public void reload(ResourceManager manager) {
        conductiveBlocksMap.clear(); // Очищаем карту перед загрузкой
        String folderPath = "molten_vents/blocks/conductive"; // Папка с JSON-файлами

        for (Identifier id : manager.findResources(folderPath, path -> path.getPath().endsWith(".json")).keySet()) {
            try {
                List<Resource> resources = manager.getAllResources(id);
                for (Resource resource : resources) {
                    try (InputStream stream = resource.getInputStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                        JsonElement json = JsonParser.parseReader(reader);
                        conductiveBlocksMap.put(id, json);
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка при загрузке " + id + ": " + e.getMessage());
            }
        }

        System.out.println("Загружено " + conductiveBlocksMap.size() + " conductive блоков");
    }
}
