package unit;

import ae.cyberspeed.board.GameBoardImpl;
import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.reward.*;
import ae.cyberspeed.symbol.Symbol;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import util.TestConfigResolverImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WinCombinationsValidatorTests {

    List<GameConfig.WinCombination> combinations;
    HashMap<String, Symbol> symbols;
    GameConfig config;

    @BeforeEach
    void setup() {
        combinations = new ArrayList<>() {{
            add(new GameConfig.WinCombination(
                    "same_symbol_3_times",
                    1,
                    GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS,
                    3,
                    GameConfig.WinCombination.WinCombinationGroup.SAME_SYMBOLS));
            add(new GameConfig.WinCombination(
                    "same_symbol_4_times",
                    1.5,
                    GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS,
                    4,
                    GameConfig.WinCombination.WinCombinationGroup.SAME_SYMBOLS));
            add(new GameConfig.WinCombination(
                    "same_symbol_5_times",
                    2,
                    GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS,
                    5,
                    GameConfig.WinCombination.WinCombinationGroup.SAME_SYMBOLS));
            add(new GameConfig.WinCombination(
                    "same_symbol_6_times",
                    3,
                    GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS,
                    6,
                    GameConfig.WinCombination.WinCombinationGroup.SAME_SYMBOLS));
            add(new GameConfig.WinCombination(
                    "same_symbol_7_times",
                    5,
                    GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS,
                    7,
                    GameConfig.WinCombination.WinCombinationGroup.SAME_SYMBOLS));
            add(new GameConfig.WinCombination(
                    "same_symbol_8_times",
                    10,
                    GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS,
                    8,
                    GameConfig.WinCombination.WinCombinationGroup.SAME_SYMBOLS));
            add(new GameConfig.WinCombination(
                    "same_symbol_9_times",
                    20,
                    GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS,
                    9,
                    GameConfig.WinCombination.WinCombinationGroup.SAME_SYMBOLS));

            add(new GameConfig.WinCombination(
                    "same_symbols_horizontally",
                    2,
                    GameConfig.WinCombination.WinCombinationType.LINEAR_SYMBOLS,
                    GameConfig.WinCombination.WinCombinationGroup.HORIZONTALLY_LINEAR_SYMBOLS,
                    new GameConfig.WinCombination.Cell[][] {
                            {
                                new GameConfig.WinCombination.Cell(0, 0),
                                new GameConfig.WinCombination.Cell(0, 1),
                                new GameConfig.WinCombination.Cell(0, 2),
                            },
                            {
                                new GameConfig.WinCombination.Cell(1, 0),
                                new GameConfig.WinCombination.Cell(1, 1),
                                new GameConfig.WinCombination.Cell(1, 2),
                            },
                            {
                                new GameConfig.WinCombination.Cell(2, 0),
                                new GameConfig.WinCombination.Cell(2, 1),
                                new GameConfig.WinCombination.Cell(2, 2),
                            },
                    }));
            add(new GameConfig.WinCombination(
                    "same_symbols_vertically",
                    2,
                    GameConfig.WinCombination.WinCombinationType.LINEAR_SYMBOLS,
                    GameConfig.WinCombination.WinCombinationGroup.VERTICALLY_LINEAR_SYMBOLS,
                    new GameConfig.WinCombination.Cell[][] {
                            {
                                new GameConfig.WinCombination.Cell(0, 0),
                                new GameConfig.WinCombination.Cell(1, 0),
                                new GameConfig.WinCombination.Cell(2, 0),
                            },
                            {
                                new GameConfig.WinCombination.Cell(0, 1),
                                new GameConfig.WinCombination.Cell(1, 1),
                                new GameConfig.WinCombination.Cell(2, 1),
                            },
                            {
                                new GameConfig.WinCombination.Cell(0, 2),
                                new GameConfig.WinCombination.Cell(1, 2),
                                new GameConfig.WinCombination.Cell(2, 2),
                            },
                    }));
            add(new GameConfig.WinCombination(
                    "same_symbols_diagonally_left_to_right",
                    5,
                    GameConfig.WinCombination.WinCombinationType.LINEAR_SYMBOLS,
                    GameConfig.WinCombination.WinCombinationGroup.LTR_DIAGONALLY_LINEAR_SYMBOLS,
                    new GameConfig.WinCombination.Cell[][] {
                            {
                                new GameConfig.WinCombination.Cell(0, 0),
                                new GameConfig.WinCombination.Cell(1, 1),
                                new GameConfig.WinCombination.Cell(2, 2),
                            },
                    }));
            add(new GameConfig.WinCombination(
                    "same_symbols_diagonally_right_to_left",
                    5,
                    GameConfig.WinCombination.WinCombinationType.LINEAR_SYMBOLS,
                    GameConfig.WinCombination.WinCombinationGroup.RTL_DIAGONALLY_LINEAR_SYMBOLS,
                    new GameConfig.WinCombination.Cell[][] {
                            {
                                new GameConfig.WinCombination.Cell(0, 2),
                                new GameConfig.WinCombination.Cell(1, 1),
                                new GameConfig.WinCombination.Cell(2, 0),
                            },
                    }));
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
    }

    @Nested
    @DisplayName("WinCombinationsValidator.validateCombinations() tests ")
    class ValidateCombinationsTests {

        @Test
        @DisplayName("WinCombinationsValidator.validateCombinations() returns a reward for a lost game")
        void Should_returnAnEntryWithAnEmptyListForAllSymbols() {
            val values = new String[][]{
                    {"A", "B", "C"},
                    {"D", "E", "F"},
                    {"A", "B", "C"}
            };

            val board = mock(GameBoardImpl.class);
            when(board.asValueMatrix()).thenReturn(values);

            val validator = new WinCombinationsValidatorImpl(config, board);

            val executables = new ArrayList<Executable>();

            for (Symbol s : symbols.values()) {
                executables.add(() -> {
                    Map.Entry<Symbol, List<GameConfig.WinCombination>> e = validator.validate(s);
                    assertTrue(e.getValue().isEmpty());
                });
            }

            assertAll(executables);
        }

        @Test
        @DisplayName("WinCombinationsValidator.validateCombinations() returns a reward for a lost game")
        void Should_returnCorrectCombinationsForSymbolsThatHaveWinningCombinations() {
            val values = new String[][]{
                    {"A", "A", "A"},
                    {"D", "A", "A"},
                    {"A", "B", "A"}
            };

            val board = mock(GameBoardImpl.class);
            when(board.asValueMatrix()).thenReturn(values);
            when(board.getEntry(any(Integer.class), any(Integer.class)))
                    .thenAnswer(invocation -> {
                        int r = invocation.getArgument(0);
                        int c = invocation.getArgument(1);
                        return symbols.get(values[r][c]);
                    });
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

            val validator = new WinCombinationsValidatorImpl(config, board);

            val executables = new ArrayList<Executable>();

            for (Symbol s : symbols.values()) {
                executables.add(() -> {
                    Map.Entry<Symbol, List<GameConfig.WinCombination>> e = validator.validate(s);
                    val symbol = e.getKey();
                    val list = e.getValue();

                    if (symbol.equals(symbols.get("A"))) {
                        assertAll(
                                () -> assertTrue(list.contains(combinations.get(4))),
                                () -> assertTrue(list.contains(combinations.get(7))),
                                () -> assertTrue(list.contains(combinations.get(8))),
                                () -> assertTrue(list.contains(combinations.get(9))),
                                () -> assertTrue(list.contains(combinations.get(10))));
                    } else {
                        assertTrue(list.isEmpty());
                    }
                });
            }

            assertAll(executables);
        }
    }
}
