package ae.cyberspeed;

import ae.cyberspeed.board.GameBoard;
import ae.cyberspeed.board.GameBoardImpl;
import ae.cyberspeed.config.ConfigDeserializer;
import ae.cyberspeed.config.ConfigResolver;
import ae.cyberspeed.config.ConfigResolverImpl;
import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.exception.ArgsFormatException;
import ae.cyberspeed.game.GameImpl;
import ae.cyberspeed.game.ProbabilityMatrix;
import ae.cyberspeed.reward.*;
import ae.cyberspeed.symbol.SymbolFactory;
import ae.cyberspeed.symbol.SymbolFactoryImpl;
import ae.cyberspeed.util.GsonProvider;
import ae.cyberspeed.util.GsonProviderImpl;
import com.google.gson.Gson;

import java.util.Map;

public class Starter {

    public static void main(String[] args) {
        final String CONFIG = "--config";
        final String BETTING_AMOUNT = "--betting-amount";

        String prevArg = null;
        StringBuilder path = new StringBuilder();
        double bettingAmount = -1;

        for (String s : args) {
            if (s.equals(CONFIG) || s.equals(BETTING_AMOUNT)) {
                prevArg = s;
                continue;
            }

            if (CONFIG.equals(prevArg)) {
                if (!path.isEmpty()) {
                    path.append(" ");
                }
                path.append(s);
            }

            if (BETTING_AMOUNT.equals(prevArg) && bettingAmount == -1) {
                bettingAmount = Double.parseDouble(s);
            }
        }

        if (path.isEmpty() || bettingAmount == -1) {
            throw new ArgsFormatException(
                    "Args format should be as follows: --config [config path] --betting-amount [betting amount]");
        }

        GsonProvider provider = new GsonProviderImpl();
        Gson gson = provider.provide(
                Map.of(Reward.class, new RewardSerializer()), Map.of(GameConfig.class, new ConfigDeserializer()));
        ConfigResolver configResolver = new ConfigResolverImpl(gson);
        GameConfig config = configResolver.resolve(path.toString());
        SymbolFactory factory = new SymbolFactoryImpl(config);
        GameBoard board = new GameBoardImpl(config);
        ImpactApplier applier = new ImpactApplierImpl();
        WinCombinationsValidator validator = new WinCombinationsValidatorImpl(config, board);
        RewardResolver rewardResolver = new RewardResolverImpl(config, board, applier, validator);
        ProbabilityMatrix probMatrix = new ProbabilityMatrix(config.rows(), config.columns(), config.probabilities());

        GameImpl g = new GameImpl(config, factory, board, rewardResolver, probMatrix);
        g.start();
        g.generateResults();
        Reward r = g.calculateReward(bettingAmount);

        System.out.println(gson.toJson(r));
    }
}