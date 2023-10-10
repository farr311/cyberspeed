package ae.cyberspeed.reward;

import ae.cyberspeed.board.GameBoard;
import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.symbol.Symbol;

import java.util.*;
import java.util.stream.Collectors;

public class RewardResolverImpl implements RewardResolver {

    private final Map<String, Symbol> symbols;
    private final GameBoard board;

    private final WinCombinationsValidator validator;
    private final ImpactApplier applier;

    public RewardResolverImpl(
            GameConfig config,
            GameBoard board,
            ImpactApplier applier,
            WinCombinationsValidator validator) {

        this.symbols = config.symbols();
        this.board = board;
        this.validator = validator;
        this.applier = applier;
    }

    @Override
    public Reward resolve(double bettingAmount) {
        var bettingAmountWrapper = new Object() { final double amount = bettingAmount; };
        var rewardWrapper = new Object() { double reward = 0; };

        String[][] matrix = board.asValueMatrix();

        Map<Symbol, List<GameConfig.WinCombination>> appliedWinningCombinations = symbols.values().stream()
                .filter(s -> Symbol.SymbolType.STANDARD.equals(s.getSymbolType()))
                .map(validator::validate)
                .filter(e -> !e.getValue().isEmpty())
                .peek(e -> {
                    List<Double> multipliersForSymbol = e.getValue().stream()
                            .map(GameConfig.WinCombination::getRewardMultiplier)
                            .toList();

                    double totalMultiplier = 0;

                    if (!multipliersForSymbol.isEmpty()) {
                        totalMultiplier = multipliersForSymbol.stream()
                                .reduce(e.getKey().getRewardMultiplier(), (a, b) -> a * b);
                    }

                    rewardWrapper.reward += bettingAmountWrapper.amount * totalMultiplier;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Symbol appliedBonusSymbol = symbols.values().stream()
                .filter(s -> Symbol.SymbolType.BONUS_SYMBOL.equals(s.getSymbolType()))
                .filter(validator::isBonusSymbolApplicable)
                .findAny().orElse(null);

        if (appliedWinningCombinations.isEmpty()) {
            rewardWrapper.reward = 0;
            appliedBonusSymbol = null;
        }

        double reward = applier.apply(rewardWrapper.reward, appliedBonusSymbol);

        return new Reward(matrix, reward, appliedWinningCombinations, appliedBonusSymbol);
    }
}
