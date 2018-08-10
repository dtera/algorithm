package cn.cstn.algorithm.leetcode.string;

/**
 * 657               Judge Route Circle
 * description :     Initially, there is a Robot at position (0, 0). Given a sequence of its moves,
 *                   judge if this robot makes a circle, which means it moves back to the original place.
 *                   The move sequence is represented by a string. And each move is represent by a character.
 *                   The valid robot moves are R (Right), L (Left), U (Up) and D (down).
 *                   The output should be true or false representing whether the robot makes a circle.
 * example 1:
 *                   Input: "UD"
 *                   Output: true
 * example 2:
 *                   Input: "LL"
 *                   Output: false
 * @author :        zhaohq
 * date :            2018-07-26 18:39
 */
public class JudgeCircle {
    public static void main(String[] args) {
        String moves = "UD";
        System.out.println(judgeCircle(moves));
    }

    private static boolean judgeCircle(String moves) {
        int hc = 0, vc = 0;
        for (int i = 0; i < moves.length(); i++)
            switch (moves.charAt(i)) {
                case 'L':
                    hc--;
                    break;
                case 'R':
                    hc++;
                    break;
                case 'U':
                    vc++;
                    break;
                case 'D':
                    vc--;
                    break;
                default:
                    throw new RuntimeException("input format error...");
            }

        return hc == 0 && vc == 0;
    }
}
