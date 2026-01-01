package sillicat.util.alts;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

public class Alt {
    public enum Status{
        FAILED,
        WAITING,
        LOGGING_IN,
        SUCCESS;
    }

    @Getter
    @Setter
    public String username;

    @Getter
    @Setter
    public Status status = Status.WAITING;

    public JsonObject toJson(){
        return new JsonObject();
    }

    public void login(){
        throw new UnsupportedOperationException("[UNSUPPORTED] Alt Cannot Login: Does not support login.");
    }
}
