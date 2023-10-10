package ae.cyberspeed.config;

import ae.cyberspeed.symbol.Symbol;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigDeserializer implements JsonDeserializer<GameConfig> {

    @Override
    public GameConfig deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject json = jsonElement.getAsJsonObject();

        int columns = json.get("columns").getAsInt();
        int rows = json.get("rows").getAsInt();


        Map<String, Symbol> symbols = json.getAsJsonObject("symbols").entrySet().stream()
                .map(e -> {
                    String key = e.getKey();
                    JsonObject value = e.getValue().getAsJsonObject();

                    Symbol symbol;

                    String t = value.get("type").getAsString();
                    double rewardMultiplier = value.has("reward_multiplier") ?
                            value.get("reward_multiplier").getAsDouble() : 0;

                    if (t.equals("standard")) {
                        symbol = new Symbol(key, rewardMultiplier);
                    } else {
                        Symbol.Impact impact = Symbol.Impact.valueOf(value.get("impact").getAsString().toUpperCase());
                        double extra = value.has("extra") ? value.get("extra").getAsDouble() : 0;

                        symbol = new Symbol(key, impact, rewardMultiplier, extra);
                    }

                    return Map.entry(key, symbol);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        JsonObject probabilitiesJson = json.getAsJsonObject("probabilities");
        JsonArray standardSymbolsProbabilities = probabilitiesJson.getAsJsonArray("standard_symbols");
        JsonObject bonusSymbolsProbabilities = probabilitiesJson.getAsJsonObject("bonus_symbols");

        List<GameConfig.Probabilities.Probability> standardSymbols = standardSymbolsProbabilities.asList().stream()
                .map(JsonElement::getAsJsonObject)
                .map(e -> {
                    int column = e.get("column").getAsInt();
                    int row = e.get("row").getAsInt();
                    Map<String, Integer> s = e.get("symbols").getAsJsonObject().entrySet().stream()
                            .map(entry -> Map.entry(entry.getKey(), entry.getValue().getAsInt()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    return new GameConfig.Probabilities.Probability(column, row, s);
                })
                .toList();

        Map<String, Integer> s = bonusSymbolsProbabilities.get("symbols").getAsJsonObject().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getAsInt()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        GameConfig.Probabilities.Probability bonusSymbols = new GameConfig.Probabilities.Probability(
                0, 0, s);

        GameConfig.Probabilities probabilities = new GameConfig.Probabilities(standardSymbols, bonusSymbols);

        JsonObject winCombinationsJson = json.get("win_combinations").getAsJsonObject();

        List<GameConfig.WinCombination> combinations = winCombinationsJson.entrySet().stream()
                .map(e -> {
                    JsonObject value = e.getValue().getAsJsonObject();

                    String name = e.getKey();
                    double rewardMultiplier = value.get("reward_multiplier").getAsDouble();
                    GameConfig.WinCombination.WinCombinationType when = GameConfig.WinCombination.WinCombinationType
                            .valueOf(value.get("when").getAsString().toUpperCase());
                    GameConfig.WinCombination.WinCombinationGroup group = GameConfig.WinCombination.WinCombinationGroup
                            .valueOf(value.get("group").getAsString().toUpperCase());

                    if (value.has("count")) {
                        int count = value.get("count").getAsInt();
                        return new GameConfig.WinCombination(name, rewardMultiplier, when, count, group);
                    }

                    JsonArray coveredAreasJson = value.get("covered_areas").getAsJsonArray();
                    GameConfig.WinCombination.Cell[][] coveredAreas = coveredAreasJson.asList().stream()
                            .map(a -> {
                                JsonArray cellsJson = a.getAsJsonArray();

                                return cellsJson.asList().stream()
                                        .map(element -> {
                                            String cellStr = element.getAsString();
                                            String[] values = cellStr.split(":");

                                            int row = Integer.parseInt(values[0]);
                                            int column = Integer.parseInt(values[1]);
                                            return new GameConfig.WinCombination.Cell(row, column);
                                        })
                                        .toArray(GameConfig.WinCombination.Cell[]::new);
                            })
                            .toArray(GameConfig.WinCombination.Cell[][]::new);

                    return new GameConfig.WinCombination(name, rewardMultiplier, when, group, coveredAreas);
                })
                .toList();

        return new GameConfig(columns, rows, symbols, probabilities, combinations);
    }
}
