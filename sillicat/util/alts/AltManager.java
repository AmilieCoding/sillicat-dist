package sillicat.util.alts;

import com.google.gson.*;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import sillicat.util.alts.impl.SessionAlt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AltManager {

    @Getter
    private final ArrayList<Alt> alts = new ArrayList<>();

    private final File saveFile;

    public AltManager() {
        this.saveFile = new File(Minecraft.getMinecraft().mcDataDir, "sillicat_alts.json");
        loadAlts();
        System.out.println("Loaded " + this.alts.size() + " alts.");
    }

    public void addAlt(Alt alt) {
        this.alts.add(alt);
        System.out.println("Added alt: " + alt.username);
        saveAlts();
    }

    public void removeAlt(Alt alt) {
        this.alts.remove(alt);
        saveAlts();
    }

    public void clear() {
        this.alts.clear();
        saveAlts();
    }

    public boolean alreadyIn(Alt alt) {
        for (Alt a : getAlts()) {
            if (a.toJson().equals(alt.toJson())) {
                return true;
            }
        }
        return false;
    }

    public void saveAlts() {
        JsonArray arr = new JsonArray();

        for (Alt a : alts) {
            // Only saving SessionAlt for now (matches your posted SessionAlt.toJson()).
            if (a instanceof SessionAlt) {
                arr.add(((SessionAlt) a).toJson());
            }
        }

        try {
            File parent = saveFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            try (Writer w = new OutputStreamWriter(new FileOutputStream(saveFile), StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(arr, w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAlts() {
        if (!saveFile.exists()) return;

        try (Reader r = new InputStreamReader(new FileInputStream(saveFile), StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(r);
            if (!root.isJsonArray()) return;

            JsonArray arr = root.getAsJsonArray();

            alts.clear();

            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject o = el.getAsJsonObject();

                String type = o.has("type") ? o.get("type").getAsString() : "";
                if (!"session".equalsIgnoreCase(type)) continue;

                String refreshToken = o.has("refreshToken") ? o.get("refreshToken").getAsString() : "";
                long lastLogin = o.has("lastLogin") ? o.get("lastLogin").getAsLong() : 0L;
                String username = o.has("username") ? o.get("username").getAsString() : "Unknown";
                String uuid = o.has("uuid") ? o.get("uuid").getAsString() : "";

                alts.add(new SessionAlt(refreshToken, lastLogin, username, uuid));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SessionAlt getLastSessionAlt() {
        SessionAlt best = null;
        for (Alt a : alts) {
            if (a instanceof SessionAlt) {
                SessionAlt sa = (SessionAlt) a;
                if (best == null || sa.lastLogin > best.lastLogin) {
                    best = sa;
                }
            }
        }
        return best;
    }

    public void autoLoginLastSession() {
        SessionAlt last = getLastSessionAlt();
        if (last != null) {
            System.out.println("Auto-login: attempting last session alt: " + last.getUsername());
            last.login();
        } else {
            System.out.println("Auto-login: no session alts found.");
        }
    }

    public File getSaveFile() {
        return saveFile;
    }
}
