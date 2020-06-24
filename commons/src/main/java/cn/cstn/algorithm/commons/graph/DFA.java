package cn.cstn.algorithm.commons.graph;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

/**
 * DFA
 *
 * @author zhaohuiqiang
 * @date 2020/6/24 14:04
 */
@Builder
@RequiredArgsConstructor
public class DFA {
    private final int[][] transTable;
    private final List<Integer> legalStates;


    public boolean accept(String content) {
        return accept(content, CharType::of);
    }

    public <E extends Enum<E>> boolean accept(String content, Function<Character, Enum<E>> function) {
        int state = 0;
        for (int i = 0; i < content.length(); i++) {
            int col = function.apply(content.charAt(i)).ordinal();
            state = transTable[state][col];
            if (state == -1) return false;
        }

        return legalStates.contains(state);
    }

    public static DFA of(int[][] transTable, List<Integer> legalStates) {
        return DFA.builder()
                .transTable(transTable)
                .legalStates(legalStates)
                .build();
    }

    @RequiredArgsConstructor
    @Getter
    private enum CharType {
        SIGN("sign(+/-)") {
            @Override
            boolean match(char c) {
                return c == '+' || c == '-';
            }
        },
        DIGIT("digit(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)") {
            @Override
            boolean match(char c) {
                return c >= '0' && c <= '9';
            }
        },
        EXP("exponent(e/E)") {
            @Override
            boolean match(char c) {
                return c == 'e' || c == 'E';
            }
        },
        DOT("dot(.)") {
            @Override
            boolean match(char c) {
                return c == '.';
            }
        },
        BLANK("blank( )") {
            @Override
            boolean match(char c) {
                return c == ' ';
            }
        },
        OTHER("other(alphabet, _, -, * ...)") {
            @Override
            boolean match(char c) {
                for (CharType charType : CharType.values()) {
                    if (charType != CharType.OTHER && charType.match(c)) {
                        return false;
                    }
                }
                return true;
            }
        };

        private final String desc;

        abstract boolean match(char c);

        public static CharType of(char c) {
            for (CharType charType : CharType.values()) {
                if (charType.match(c)) {
                    return charType;
                }
            }
            return CharType.OTHER;
        }
    }

}
