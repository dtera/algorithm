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

    public <E extends Enum<E> & SymbolType> boolean accept(String content, Function<Character, Enum<E>> function) {
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
    public enum CharType implements SymbolType {
        SIGN("sign(+/-)") {
            @Override
            public boolean match(char c) {
                return c == '+' || c == '-';
            }
        },
        DIGIT("digit(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)") {
            @Override
            public boolean match(char c) {
                return c >= '0' && c <= '9';
            }
        },
        EXP("exponent(e/E)") {
            @Override
            public boolean match(char c) {
                return c == 'e' || c == 'E';
            }
        },
        DOT("dot(.)") {
            @Override
            public boolean match(char c) {
                return c == '.';
            }
        },
        BLANK("blank( )") {
            @Override
            public boolean match(char c) {
                return c == ' ';
            }
        },
        OTHER("other(alphabet, _, -, * ...)") {
            @Override
            public boolean match(char c) {
                return matchOther(c, CharType.values(), CharType.OTHER);
            }
        };

        private final String desc;

        public static CharType of(char c) {
            return SymbolType.of(c, CharType.values());
        }

    }

    public interface SymbolType {

        boolean match(char c);

        default <E extends Enum<E> & SymbolType> boolean matchOther(char c, E[] symbolTypes, E otherType) {
            for (E symbolType : symbolTypes) {
                if (symbolType != otherType && symbolType.match(c)) {
                    return false;
                }
            }
            return true;
        }

        static <E extends Enum<E> & SymbolType> E of(char c, E[] symbolTypes) {
            for (E symbolType : symbolTypes) {
                if (symbolType.match(c)) {
                    return symbolType;
                }
            }

            return null;
        }

    }

}
