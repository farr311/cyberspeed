package ae.cyberspeed.config;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

public class ConfigResolverImpl implements ConfigResolver {

    private final Gson gson;

    public ConfigResolverImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public GameConfig resolve(String path) {
        try (FileReader r = new FileReader(path)){
            return gson.fromJson(r, GameConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("File not found");
        }

    }
}
