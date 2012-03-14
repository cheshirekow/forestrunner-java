package edu.mit.lids.ares.forestrunner.screens;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonHighScoreRowDeserializer
    implements  JsonDeserializer<HighScoreRow>
                
{
    @Override
    public HighScoreRow deserialize(
            JsonElement json, 
            Type typeOfT, 
            JsonDeserializationContext context) throws JsonParseException 
    {
        HighScoreRow result = new HighScoreRow();
        result.user_nick    = json.getAsJsonObject().get("user_nick").getAsString();
        result.date         = json.getAsJsonObject().get("date").getAsString();
        result.score        = json.getAsJsonObject().get("score").getAsString();
        return result;
    }
}

