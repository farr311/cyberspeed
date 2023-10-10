package ae.cyberspeed.config;

public interface ConfigResolver {

    GameConfig resolve(String path);
}
