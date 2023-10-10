package ae.cyberspeed.reward;

import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.symbol.Symbol;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public record Reward(String[][] matrix, double reward,
                     @SerializedName("applied_winning_combinations") Map<Symbol, List<GameConfig.WinCombination>> appliedWinningCombinations,
                     @SerializedName("applied_bonus_symbol") Symbol appliedBonusSymbol) {
}
