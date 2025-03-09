package com.deokma.MoltenVents.config;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "molten_vents")
public class CommonConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean useSource = true;

    @ConfigEntry.Gui.Tooltip
    public int ventDepth = 10;

    @ConfigEntry.Gui.Tooltip
    public boolean generateUnderwater = false;

    public CommonConfig getDefault() {
        return new CommonConfig();
    }

    public static void register() {
        AutoConfig.register(CommonConfig.class, GsonConfigSerializer::new);
    }

    public static CommonConfig get() {
        return AutoConfig.getConfigHolder(CommonConfig.class).getConfig();
    }
}
