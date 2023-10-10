package ae.cyberspeed.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonProviderImpl implements GsonProvider {
    @Override
    public Gson provide(Map<Type, JsonSerializer<?>> serializers, Map<Type, JsonDeserializer<?>> deserializers) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        for (Map.Entry<Type, JsonSerializer<?>> e: serializers.entrySet()) {
            gsonBuilder.registerTypeAdapter(e.getKey(), e.getValue());
        }
        for (Map.Entry<Type, JsonDeserializer<?>> e: deserializers.entrySet()) {
            gsonBuilder.registerTypeAdapter(e.getKey(), e.getValue());
        }

        return gsonBuilder.create();
    }
}
