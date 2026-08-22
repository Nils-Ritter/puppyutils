package de.puppyutils.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path FILE =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("puppyutils_config.json");

    private static Config config = new Config();

    private ConfigManager() {
    }

    public static Config get() {
        return config;
    }

    public static void load() {
        if (!Files.exists(FILE)) {
            config = new Config();
            save();
            return;
        }

        try {
            String json = Files.readString(FILE);

            Config loaded = GSON.fromJson(json, Config.class);

            if (loaded != null) {
                config = loaded;
            }

        } catch (Exception e) {
            System.err.println(
                    "[PuppyUtils] Failed to load configuration"
            );

            e.printStackTrace();

            config = new Config();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());

            Files.writeString(
                    FILE,
                    GSON.toJson(config)
            );

        } catch (IOException e) {
            System.err.println(
                    "[PuppyUtils] Failed to save configuration"
            );

            e.printStackTrace();
        }
    }

    public static void reset() {
        config.reset();
        save();
    }
}
