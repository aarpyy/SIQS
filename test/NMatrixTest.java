import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NMatrixTest {

    @Test
    void append() {
        NMatrix matrix = new NMatrix();
        int[][] tempMat = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        for (int i = 0; i < 4; i++) {
            matrix.append(new NArray(tempMat[i]));
        }

        for (int i = 0; i < 4; i++) {
            assertTrue(matrix.get(i).equals(tempMat[i]));
        }
    }

    @Test
    void transpose() {
        NMatrix matrix = new NMatrix(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}});
        for (NArray a : matrix) {
            System.out.println(a.toString());
        }
        System.out.println("----");
        NMatrix T = matrix.transpose();
        System.out.println(T.toString());
    }

    @Test
    void kernel() {
        int[][] A = {{1, 0, -3, 0, 2, -8},
                {0, 1, 5, 0, -1, 4},
                {0, 0, 0, 1, 7, -9},
                {0, 0, 0, 0, 0, 0}};
        NMatrix matrix = new NMatrix(A);

        NMatrix kernel = matrix.kernel();

        int[][] expKernel = {{3, -5, 1, 0, 0, 0},
                {-2, 1, 0, -7, 1, 0},
                {8, -4, 0, 9, 0, 1}};
        assertTrue(kernel.equals(expKernel));
    }
}