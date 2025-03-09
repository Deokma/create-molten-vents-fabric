package com.deokma.MoltenVents;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MoltenVentsJsonReader {
    public static List<String> customBlocks;

    public static void main() throws Exception {
        Path moltenVentsCustomBlocks =
                FabricLoader.getInstance().getConfigDir().resolve("molten_vents_custom_blocks.json");
        Gson gson = new Gson();

        if (!Files.exists(moltenVentsCustomBlocks)) {
            try (JsonWriter writer = new JsonWriter(new FileWriter(moltenVentsCustomBlocks.toFile()))) {
                JsonObject defaultData =
                        gson.fromJson("{\"values\":[\"asurine\",\"veridium\",\"crimsite\"," +
                                "\"ochrum\",\"scorchia\",\"scoria\"]}", JsonObject.class);
                gson.toJson(defaultData, writer);
            }
        }

        try (JsonReader reader = new JsonReader(new FileReader(moltenVentsCustomBlocks.toFile()))) {
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            JsonArray customBlockNames = data.getAsJsonArray("values");
            List<String> tempCustomBlocks = new ArrayList<>();
            customBlockNames.forEach(element -> tempCustomBlocks.add(element.getAsString()));
            customBlocks = tempCustomBlocks;
        }
    }
}
