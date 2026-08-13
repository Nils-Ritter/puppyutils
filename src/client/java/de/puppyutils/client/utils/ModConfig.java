package de.puppyutils.client.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("puppyutils_config.json");

    public boolean enabled = true;
    public boolean debug = true;
    public int maxItems = 32;
    public double speed = 1.5;

    public static ModConfig load() {
        try {
            if (Files.notExists(PATH)) {
                ModConfig config = new ModConfig();
                config.save();
                return config;
            }
            return GSON.fromJson(Files.readString(PATH), ModConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(this),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }
}
