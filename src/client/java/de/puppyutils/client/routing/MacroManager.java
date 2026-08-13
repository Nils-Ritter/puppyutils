package de.puppyutils.client.routing;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public final class MacroManager {
    private static final Path MACRO_DIR = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("puppyutils")
        .resolve("macros");

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(MacroAction.class, new MacroActionAdapter())
        .create();

    private final ArrayList<MacroAction> loadedActions = new ArrayList<>();

    public List<MacroAction> getLoadedActions() {
        return loadedActions;
    }

    public void clearActions() {
        loadedActions.clear();
    }

    public void addAction(MacroAction action) {
        loadedActions.add(action);
    }

    public void addWalkAction(BlockPos pos) {
        loadedActions.add(new WalkAction(pos.getX(), pos.getY(), pos.getZ()));
    }

    public void addWalkAction(int x, int y, int z) {
        loadedActions.add(new WalkAction(x, y, z));
    }

    public void addWaitAction(int ticks) {
        loadedActions.add(new WaitAction(ticks));
    }

    public boolean saveMacro(String name) throws IOException {
        Files.createDirectories(MACRO_DIR);
        Path file = MACRO_DIR.resolve(sanitizeFileName(name) + ".json");

        Type listType = new TypeToken<ArrayList<MacroAction>>() {}.getType();
        String json = GSON.toJson(loadedActions, listType);

        Files.writeString(
            file,
            json,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        );
        return true;
    }

    public boolean loadMacro(String name) throws IOException {
        Path file = MACRO_DIR.resolve(sanitizeFileName(name) + ".json");
        if (!Files.exists(file)) {
            return false;
        }

        String json = Files.readString(file, StandardCharsets.UTF_8);
        Type listType = new TypeToken<ArrayList<MacroAction>>() {}.getType();
        ArrayList<MacroAction> actions = GSON.fromJson(json, listType);

        loadedActions.clear();
        if (actions != null) {
            loadedActions.addAll(actions);
        }
        return true;
    }

    private static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static final class MacroActionAdapter
        implements JsonSerializer<MacroAction>, JsonDeserializer<MacroAction> {

        @Override
        public JsonElement serialize(
            MacroAction src,
            Type typeOfSrc,
            JsonSerializationContext context
        ) {
            JsonObject obj = context.serialize(src, src.getClass()).getAsJsonObject();
            obj.addProperty("type", src.type());
            return obj;
        }

        @Override
        public MacroAction deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
        ) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            JsonElement typeElement = obj.get("type");
            if (typeElement == null || typeElement.isJsonNull()) {
                throw new JsonParseException("Macro action is missing required field 'type': " + obj);
            }

            String type = typeElement.getAsString();

            return switch (type) {
                case "walk" -> context.deserialize(obj, WalkAction.class);
                case "wait" -> context.deserialize(obj, WaitAction.class);
                default -> throw new JsonParseException("Unknown macro action type: " + type);
            };
        }
    }

    /*
    Global Macro state
    */

    public static Boolean MacroRunning;
    public static enum MacroTypes {
        Farming,
        Mining,
        Foraging,
        Fishing,
        Combat,
    };
}
