package edu.mit.lids.ares.forestrunner.screens;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonNickChangeResultDeserializer
    implements  JsonDeserializer<NickChangeResult>
                
{
    @Override
    public NickChangeResult deserialize(
            JsonElement json, 
            Type typeOfT, 
            JsonDeserializationContext context) throws JsonParseException 
    {
        NickChangeResult result = new NickChangeResult();
        result.status       = json.getAsJsonObject().get("status").getAsString();
        result.message      = json.getAsJsonObject().get("message").getAsString();
        result.nick         = json.getAsJsonObject().get("nick").getAsString();
        return result;
    }
}

