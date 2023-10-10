package ae.cyberspeed.game;

import ae.cyberspeed.board.GameBoard;
import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.reward.Reward;
import ae.cyberspeed.reward.RewardResolver;
import ae.cyberspeed.symbol.Symbol;
import ae.cyberspeed.symbol.SymbolFactory;


public class GameImpl implements Game {

    private final GameBoard board;
    private final GameConfig config;

    private final SymbolFactory factory;

    private final RewardResolver resolver;

    private final ProbabilityMatrix probMatrix;

    public GameImpl(GameConfig config,
                    SymbolFactory factory,
                    GameBoard board,
                    RewardResolver resolver,
                    ProbabilityMatrix probMatrix) {

        this.config = config;
        this.factory = factory;
        this.board = board;
        this.resolver = resolver;
        this.probMatrix = probMatrix;
    }

    @Override
    public void start() {
        board.generateBoard();
    }

    @Override
    public void generateResults() {
        Symbol[][] supplyingMatrix = generateSupplyingMatrix();
        board.updateBoard(supplyingMatrix);
    }

    @Override
    public Reward calculateReward(double bettingAmount) {
        return resolver.resolve(bettingAmount);
    }

    private Symbol[][] generateSupplyingMatrix() {

        int numColumns = config.columns();
        int numRows = config.rows();

        Symbol[][] matrix = new Symbol[numRows][numColumns];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                matrix[i][j] = factory.createSymbol(probMatrix.getCell(i, j).nextWeightedRandomValue());
            }
        }

        return matrix;
    }

}
