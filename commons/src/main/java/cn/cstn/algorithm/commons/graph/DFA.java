package cn.cstn.algorithm.commons.graph;

import cn.cstn.algorithm.commons.util.ArrayUtil;
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
        return accept(ArrayUtil.asList(content.toCharArray()), CharType::of);
    }

    public <T, E extends Enum<E> & SymbolType<T>> boolean accept(Iterable<T> content, Function<T, Enum<E>> function) {
        int state = 0;
        for (T t : content) {
            int col = function.apply(t).ordinal();
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
    public enum CharType implements SymbolType<Character> {
        SIGN("sign(+/-)") {
            @Override
            public boolean match(Character c) {
                return c == '+' || c == '-';
            }
        },
        DIGIT("digit(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)") {
            @Override
            public boolean match(Character c) {
                return c >= '0' && c <= '9';
            }
        },
        EXP("exponent(e/E)") {
            @Override
            public boolean match(Character c) {
                return c == 'e' || c == 'E';
            }
        },
        DOT("dot(.)") {
            @Override
            public boolean match(Character c) {
                return c == '.';
            }
        },
        BLANK("blank( )") {
            @Override
            public boolean match(Character c) {
                return c == ' ';
            }
        },
        OTHER("other(alphabet, _, -, * ...)") {
            @Override
            public boolean match(Character c) {
                return matchOther(c, CharType.values());
            }
        };

        private final String desc;

        public static CharType of(Character c) {
            return SymbolType.of(c, CharType.values());
        }

    }

    public interface SymbolType<T> {

        boolean match(T c);

        default <E extends Enum<E> & SymbolType<T>> boolean matchOther(T c, E[] symbolTypes) {
            for (E symbolType : symbolTypes) {
                if (symbolType != this && symbolType.match(c)) {
                    return false;
                }
            }
            return true;
        }

        static <T, E extends Enum<E> & SymbolType<T>> E of(T c, E[] symbolTypes) {
            for (E symbolType : symbolTypes) {
                if (symbolType.match(c)) {
                    return symbolType;
                }
            }

            return null;
        }

    }

}
