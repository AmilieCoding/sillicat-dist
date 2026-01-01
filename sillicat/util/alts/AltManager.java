package sillicat.util.alts;

import java.util.ArrayList;

import sillicat.Sillicat.*;
import lombok.Getter;

public class AltManager {
    // TODO : Replace the login methods with PvpCafe's library when it's ready

    @Getter
    public ArrayList<Alt> alts = new ArrayList<>();

    public AltManager() {
        System.out.println("Loaded " + this.alts.size() + " alts.");
    }

    public void addAlt(Alt alt) {
        this.alts.add(alt);
        System.out.println("Added alt: " + alt.username);
    }

    public boolean alreadyIn(Alt alt) {
        for (Alt a : getAlts()) {
            if (a.toJson().equals(alt.toJson())) {
                return true;
            }
        }
        return false;
    }
}