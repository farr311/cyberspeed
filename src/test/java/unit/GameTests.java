package unit;

import ae.cyberspeed.board.GameBoardImpl;
import ae.cyberspeed.game.GameImpl;
import ae.cyberspeed.game.ProbabilityMatrix;

import ae.cyberspeed.symbol.Symbol;
import ae.cyberspeed.symbol.SymbolFactoryImpl;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import util.TestConfigResolverImpl;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameTests {

    @Nested
    @DisplayName("Game start tests")
    class StartTests {

        @Test
        @DisplayName("Game generates the supplying matrix correctly")
        void Should_populateTheBoardWithGivenValues() {
            val c = 3;
            val r = 3;

            val configResolver = TestConfigResolverImpl.Builder()
                    .setColumns(c)
                    .setRows(r)
                    .build();

            val config = configResolver.resolve();

            String[][] cells = new String[][] {
                    {"A", "B", "C"},
                    {"A", "B", "C"},
                    {"A", "B", "C"},
            };


            val matrix = mock(ProbabilityMatrix.class);
            when(matrix.getCell(any(Integer.class), any(Integer.class)))
                    .thenAnswer(invocation -> {
                        val i = invocation.getArgument(0, Integer.class);
                        val j = invocation.getArgument(1, Integer.class);
                        val mockCell = mock(ProbabilityMatrix.ProbabilityCell.class);
                        when(mockCell.nextWeightedRandomValue())
                                .thenReturn(cells[i][j]);

                        return mockCell;
                    });

            val factory = mock(SymbolFactoryImpl.class);
            when(factory.createSymbol(any(String.class)))
                    .thenAnswer(invocation -> new Symbol(invocation.getArgument(0, String.class)));

            val board = mock(GameBoardImpl.class);
            doAnswer(invocation -> {
                val arr = invocation.getArgument(0, Symbol[][].class);
                val executables = new ArrayList<Executable>();

                for (int i = 0; i < arr.length; i++) {
                    for (int j = 0; j < arr.length; j++) {
                        int finalI = i;
                        int finalJ = j;
                        executables.add(() -> assertEquals(cells[finalI][finalJ], arr[finalI][finalJ].getValue()));
                    }
                }

                assertAll(executables);
                return null;
            }).when(board).updateBoard(any(Symbol[][].class));

            val game = new GameImpl(config, factory, board, null, matrix);
            game.start();
            game.generateResults();
        }

    }

}
