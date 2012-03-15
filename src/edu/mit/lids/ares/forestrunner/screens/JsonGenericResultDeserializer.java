package edu.mit.lids.ares.forestrunner.screens;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonGenericResultDeserializer
    implements  JsonDeserializer<GenericResult>
                
{
    @Override
    public GenericResult deserialize(
            JsonElement json, 
            Type typeOfT, 
            JsonDeserializationContext context) throws JsonParseException 
    {
        GenericResult result = new GenericResult();
        result.status       = json.getAsJsonObject().get("status").getAsString();
        result.message      = json.getAsJsonObject().get("message").getAsString();
        return result;
    }
}

