package ae.cyberspeed.util;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public interface GsonProvider {

    Gson provide(Map<Type, JsonSerializer<?>> serializers, Map<Type, JsonDeserializer<?>> deserializers);
}
