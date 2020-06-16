package cn.cstn.algorithm.offer;

/**
 * 请设计一个函数，用来判断在一个矩阵中是否存在一条包含某字符串所有字符的路径。路径可以从矩阵中的任意一格开始，
 * 每一步可以在矩阵中向左、右、上、下移动一格。如果一条路径经过了矩阵的某一格，那么该路径不能再次进入该格子。
 * 例如，在下面的3×4的矩阵中包含一条字符串“bfce”的路径（路径中的字母用加粗标出）。
 * <p>
 * [["a","b","c","e"],
 * <p>
 * ["s","f","c","s"],
 * <p>
 * ["a","d","e","e"]]
 *
 * <p>
 * 但矩阵中不包含字符串“abfb”的路径，因为字符串的第一个字符b占据了矩阵中的第一行第二个格子之后，路径不能再次进入这个格子。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
 * <p>
 * 输出：true
 * <p>
 * <p>
 * 示例 2：
 * <p>
 * 输入：board = [["a","b"],["c","d"]], word = "abcd"
 * <p>
 * 输出：false
 * <p>
 * <p>
 * 提示：
 * <p>
 * 1 <= board.length <= 200
 * <p>
 * 1 <= board[i].length <= 200
 *
 * @author zhaohuiqiang
 * @date 2020/6/15 10:23
 */
public class PathInMatrix {

    public static void main(String[] args) {
        char[][] board = {{'a', 'b', 'c', 'e'},
                {'s', 'f', 'c', 's'},
                {'a', 'd', 'e', 'e'}};
        String word = "bfce";
        System.out.println(exist(board, word));
    }

    public static boolean exist(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (dfs(board, i, j, word, 0)) return true;
            }
        }
        return false;
    }

    private static boolean dfs(char[][] board, int i, int j, String word, int k) {
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || board[i][j] != word.charAt(k)) return false;
        if (k == word.length() - 1) return true;

        char t = board[i][j];
        board[i][j] = '*';
        boolean flag = dfs(board, i + 1, j, word, k + 1) || dfs(board, i, j + 1, word, k + 1) ||
                dfs(board, i - 1, j, word, k + 1) || dfs(board, i, j - 1, word, k + 1);
        board[i][j] = t;

        return flag;
    }

}
