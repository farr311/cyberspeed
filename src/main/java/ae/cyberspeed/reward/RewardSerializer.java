package ae.cyberspeed.reward;

import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.symbol.Symbol;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RewardSerializer implements JsonSerializer<Reward> {
    
    @Override
    public JsonElement serialize(Reward reward, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        String[][] matrix = reward.matrix();
        double rewardValue = reward.reward();
        Map<Symbol, List<GameConfig.WinCombination>> combinations = reward.appliedWinningCombinations();
        Symbol appliedBonusSymbol = reward.appliedBonusSymbol();

        JsonArray mappedMatrix = new JsonArray();
        for (String[] row : matrix) {
            JsonArray a = new JsonArray();
            for (String s : row) {
                a.add(s);
            }
            mappedMatrix.add(a);
        }

        JsonObject mappedCombinations = new JsonObject();
        combinations.entrySet().stream()
                .map(e -> {
                    JsonArray combinationNames = new JsonArray();
                    e.getValue().stream()
                            .map(GameConfig.WinCombination::getName)
                            .forEach(combinationNames::add);
                    return Map.entry(Objects.requireNonNull(e.getKey().getValue()), combinationNames);
                })
                .forEach(e -> mappedCombinations.add(e.getKey(), e.getValue()));

        String bonusSymbol = appliedBonusSymbol == null ? "null" : appliedBonusSymbol.getValue();
        
        object.add("matrix", mappedMatrix);
        object.addProperty("reward", rewardValue);

        if (rewardValue > 0) {
            object.add("applied_winning_combinations", mappedCombinations);
            object.addProperty("applied_bonus_symbol", bonusSymbol);
        }

        return object;
    }
}
