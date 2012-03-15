package edu.mit.lids.ares.forestrunner.screens;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonCreateUserResultDeserializer
    implements  JsonDeserializer<CreateUserResult>
                
{
    @Override
    public CreateUserResult deserialize(
            JsonElement json, 
            Type typeOfT, 
            JsonDeserializationContext context) throws JsonParseException 
    {
        CreateUserResult result = new CreateUserResult();
        result.status       = json.getAsJsonObject().get("status").getAsString();
        result.message      = json.getAsJsonObject().get("message").getAsString();
        result.hash         = json.getAsJsonObject().get("hash").getAsString();
        return result;
    }
}

