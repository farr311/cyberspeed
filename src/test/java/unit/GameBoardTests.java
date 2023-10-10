package unit;

import ae.cyberspeed.board.GameBoardImpl;
import ae.cyberspeed.symbol.Symbol;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import util.TestConfigResolverImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class GameBoardTests {

    @Nested
    @DisplayName("GameBoard creation tests")
    class ConstructorTests {

        @Test
        @DisplayName("Game board is created with a matrix of given size")
        void Should_createAGameBoardWithAMatrixOfGivenSize() {
            val c = 3;
            val r = 3;

            val configResolver = TestConfigResolverImpl.Builder()
                    .setColumns(c)
                    .setRows(r)
                    .build();

            val config = configResolver.resolve();

            val board = new GameBoardImpl(config);

            try {
                val matrixField = GameBoardImpl.class.getDeclaredField("matrix");
                matrixField.setAccessible(true);
                val matrix = (Symbol[][]) matrixField.get(board);

                assertAll(
                        () -> assertEquals(c, matrix.length),
                        () -> assertEquals(r, matrix[0].length));

            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
    }

    @Nested
    @DisplayName("GameBoard generation tests")
    class GenerateBoardTests {
        @Test
        @DisplayName("Every cell of the board is empty when created")
        void Should_createAGameBoardWithAllElementsEmpty() {
            val configResolver = TestConfigResolverImpl.Builder().build();
            val config = configResolver.resolve();
            val board = new GameBoardImpl(config);
            board.generateBoard();

            try {
                val matrixField = GameBoardImpl.class.getDeclaredField("matrix");
                matrixField.setAccessible(true);
                val matrix = (Symbol[][]) matrixField.get(board);

                Stream<Executable> executables = Arrays.stream(matrix)
                        .flatMap(Arrays::stream)
                        .map(value -> ()-> assertTrue(value.isEmpty()));
                assertAll(executables);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
    }

    @Nested
    @DisplayName("GameBoard update tests")
    class UpdateBoardTests {

        @Test
        @DisplayName("The board values are identical to the provided value")
        void Should_populateTheBoardWithGivenValuesInTheGivenOrder() {
            val m = new Symbol[][] {
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") },
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") },
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") }
            };

            val configResolver = TestConfigResolverImpl.Builder()
                    .setRows(m.length)
                    .setColumns(m[0].length)
                    .build();
            val config = configResolver.resolve();
            val board = new GameBoardImpl(config);
            board.generateBoard();
            board.updateBoard(m);

            try {
                val matrixField = GameBoardImpl.class.getDeclaredField("matrix");
                matrixField.setAccessible(true);
                val matrix = (Symbol[][]) matrixField.get(board);

                val executables = new ArrayList<Executable>();
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix.length; j++) {
                        int finalI = i, finalJ = j;
                        executables.add(() -> assertEquals(m[finalI][finalJ], matrix[finalI][finalJ]));
                    }
                }

                assertAll(executables);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
    }

    @Nested
    @DisplayName("GameBoard entry counting tests")
    class entryCountingTests {

        @Test
        @DisplayName("The board counts total number of entries correctly")
        void Should_returnCorrectEntryCount() {
            val m = new Symbol[][] {
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") },
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") },
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") }
            };

            val configResolver = TestConfigResolverImpl.Builder()
                    .setRows(m.length)
                    .setColumns(m[0].length)
                    .build();
            val config = configResolver.resolve();
            val board = new GameBoardImpl(config);
            board.generateBoard();
            board.updateBoard(m);

            assertAll(
                    () -> assertEquals(3, board.countEntries(new Symbol("A"))),
                    () -> assertEquals(3, board.countEntries(new Symbol("B"))),
                    () -> assertEquals(3, board.countEntries(new Symbol("C"))),
                    () -> assertEquals(0, board.countEntries(new Symbol("D")))
            );
        }
    }

    @Nested
    @DisplayName("GameBoard getAsValueMatrix tests")
    class ValueMatrixTests {

        @Test
        @DisplayName("The board counts total number of entries correctly")
        void Should_returnTheMatrixContainingTheValuesOfGivenSymbolsInGivenOrder() {
            val m = new Symbol[][] {
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") },
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") },
                    { new Symbol("A"), new Symbol("B"), new Symbol("C") }
            };

            val configResolver = TestConfigResolverImpl.Builder()
                    .setRows(m.length)
                    .setColumns(m[0].length)
                    .build();
            val config = configResolver.resolve();
            val board = new GameBoardImpl(config);
            board.generateBoard();
            board.updateBoard(m);

            val expectedMatrix = new String[][] {
                    {"A", "B", "C"},
                    {"A", "B", "C"},
                    {"A", "B", "C"}
            };

            assertArrayEquals(expectedMatrix, board.asValueMatrix());
        }
    }

}
