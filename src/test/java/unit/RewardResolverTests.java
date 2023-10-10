package unit;

import ae.cyberspeed.board.GameBoard;
import ae.cyberspeed.board.GameBoardImpl;
import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.reward.*;
import ae.cyberspeed.symbol.Symbol;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.TestConfigResolverImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RewardResolverTests {

    List<GameConfig.WinCombination> combinations;
    HashMap<String, Symbol> symbols;
    GameConfig config;
    String[][] values;
    GameBoard board;
    ImpactApplier applier;
    WinCombinationsValidator validator;
    RewardResolver resolver;

    @BeforeEach
    void setup() {
        combinations = new ArrayList<>() {{
            add(new GameConfig.WinCombination(
                    "same_symbols_3_times",
                    15,
                    GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS,
                    3,
                    GameConfig.WinCombination.WinCombinationGroup.SAME_SYMBOLS));
        }};

        symbols = new HashMap<>(){{
            put("A", new Symbol("A", 50));
            put("B", new Symbol("B", 25));
            put("C", new Symbol("C", 10));
            put("D", new Symbol("D", 5));
            put("E", new Symbol("E", 3));
            put("F", new Symbol("F", 1.5));
            put("10x", new Symbol("10x", Symbol.Impact.MULTIPLY_REWARD, 10, 0));
        }};

        val configResolver = TestConfigResolverImpl.Builder()
                .setColumns(3)
                .setRows(3)
                .setCombinations(combinations)
                .setSymbols(symbols)
                .build();

        config = configResolver.resolve();

        values = new String[][]{
                {"A", "B", "C"},
                {"E", "B", "10x"},
                {"F", "D", "B"}
        };

        board = mock(GameBoardImpl.class);
        when(board.asValueMatrix()).thenReturn(values);
        applier = mock(ImpactApplierImpl.class);
        validator = mock(WinCombinationsValidator.class);
        resolver = new RewardResolverImpl(config, board, applier, validator);
    }

    @Nested
    @DisplayName("RewardResolver.resolve tests")
    class ResolveTests {

        @Test
        @DisplayName("RewardResolver resolve returns a reward for a lost game")
        void Should_returnLostGameReward_whenNoWinningCombinationIsFound() {
            when(board.countEntries(any(Symbol.class)))
                    .thenReturn(0);

            when(applier.apply(any(Double.class), any(Symbol.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            when(validator.isBonusSymbolApplicable(any(Symbol.class))).thenReturn(false);
            when(validator.validate(any(Symbol.class)))
                    .thenAnswer(invocation -> Map.entry(invocation.getArgument(0), List.of()));

            val r = resolver.resolve(100);
            assertAll(
                    () -> assertArrayEquals(board.asValueMatrix(), r.matrix()),
                    () -> assertEquals(0, r.reward()),
                    () -> assertTrue(r.appliedWinningCombinations().isEmpty()),
                    () -> assertNull(r.appliedBonusSymbol())
            );
        }

        @Test
        @DisplayName("RewardResolver resolve returns a reward without the bonus field")
        void Should_returnARewardWithNoBonusApplied_whenNoWinCombinationFound() {
            when(board.countEntries(any(Symbol.class)))
                    .thenReturn(0);

            when(applier.apply(any(Double.class), any(Symbol.class)))
                    .thenAnswer(invocation -> {
                        double identity = invocation.getArgument(0);
                        Symbol symbol = invocation.getArgument(1);

                        return identity * symbol.getRewardMultiplier();
                    });

            when(validator.isBonusSymbolApplicable(any(Symbol.class))).thenReturn(true);
            when(validator.validate(any(Symbol.class)))
                    .thenAnswer(invocation -> Map.entry(invocation.getArgument(0), List.of()));

            val r = resolver.resolve(100);
            assertAll(
                    () -> assertArrayEquals(board.asValueMatrix(), r.matrix()),
                    () -> assertEquals(0, r.reward()),
                    () -> assertTrue(r.appliedWinningCombinations().isEmpty()),
                    () -> assertNull(r.appliedBonusSymbol())
            );
        }

        @Test
        @DisplayName("RewardResolver resolve return a reward with correctly calculated field")
        void Should_returnARewardWithCorrectValues() {
            when(board.countEntries(any(Symbol.class)))
                    .thenAnswer(invocation-> {
                        var count = 0;
                        Symbol symbol = invocation.getArgument(0);

                        for (String[] value : values) {
                            for (String s : value) {
                                if (s.equals(symbol.getValue())) {
                                    count++;
                                }
                            }
                        }

                        return count;
                    });

            when(applier.apply(any(Double.class), any(Symbol.class)))
                    .thenAnswer(invocation -> {
                        double identity = invocation.getArgument(0);
                        Symbol symbol = invocation.getArgument(1);

                        return identity * symbol.getRewardMultiplier();
                    });

            when(validator.isBonusSymbolApplicable(any(Symbol.class))).thenReturn(true);
            when(validator.validate(any(Symbol.class)))
                    .thenAnswer(invocation -> {
                        Symbol s = invocation.getArgument(0);
                        if (!"B".equals(s.getValue())) {
                            return Map.entry(s, List.of());
                        }
                        return Map.entry(s, List.of(combinations.get(0)));
                    });

            val r = resolver.resolve(100);
            val winCombinations = r.appliedWinningCombinations();
            assertAll(
                    () -> assertArrayEquals(board.asValueMatrix(), r.matrix()),
                    () -> assertEquals(375_000, r.reward()),
                    () -> assertEquals(1, winCombinations.size()),
                    () -> assertNotNull(winCombinations.get(symbols.get("B"))),
                    () -> assertFalse(winCombinations.get(symbols.get("B")).isEmpty()),
                    () -> assertEquals(combinations.get(0), winCombinations.get(symbols.get("B")).get(0)),
                    () -> assertEquals(symbols.get("10x"), r.appliedBonusSymbol())
            );
        }
    }
}
