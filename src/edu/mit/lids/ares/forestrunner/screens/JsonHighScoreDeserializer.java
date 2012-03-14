package edu.mit.lids.ares.forestrunner.screens;

import java.lang.reflect.Type;
import java.util.Arrays;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonHighScoreDeserializer
    implements  JsonDeserializer<HighScoreResult>
                
{
    @Override
    public HighScoreResult deserialize(
            JsonElement json, 
            Type typeOfT, 
            JsonDeserializationContext context) throws JsonParseException 
    {
        HighScoreResult result = new HighScoreResult();
        result.status  = json.getAsJsonObject().get("status").getAsString();
        result.message = json.getAsJsonObject().get("message").getAsString();
        HighScoreRow[] rowList = 
                context.deserialize( json.getAsJsonObject().get("user_scores"), 
                                        HighScoreRow[].class );
        result.user_scores = Arrays.asList(rowList);
        rowList = 
                context.deserialize( json.getAsJsonObject().get("global_scores"), 
                                        HighScoreRow[].class );
        result.global_scores = Arrays.asList(rowList);
        
        return result;
    }
}

