package cn.cstn.algorithm.commons.graph;

import cn.cstn.algorithm.commons.state.State;
import cn.cstn.algorithm.commons.util.ArrayUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DFA
 *
 * @author zhaohuiqiang
 * @date 2020/6/24 14:04
 */
@SuppressWarnings("unused")
@Builder
@RequiredArgsConstructor
public class DFA {
  /**
   * transition table
   */
  private final int[][] transTable;
  /**
   * legal states
   */
  private final List<Integer> legalStates;

  /**
   * whether accepts while transiting from a state to another
   *
   * @param fromState fromState
   * @param toState   toState
   * @return pair of transitive and symbols
   */
  public <S extends State> Pair<Boolean, List<CharType>> accept(S fromState, S toState) {
    return accept(fromState, toState, CharType::of);
  }

  /**
   * whether accepts while transiting from a state to another
   *
   * @param fromState  fromState
   * @param toState    toState
   * @param map2Symbol map2Symbol
   * @return pair of transitive and symbols
   */
  public <S extends State, R extends SymbolType<?>> Pair<Boolean, List<R>> accept(S fromState, S toState,
                                                                                  Function<Integer, R> map2Symbol) {
    int[] states = transTable[fromState.getIndex()];
    List<R> symbols = IntStream.range(0, transTable[0].length)
      .filter(i -> states[i] == toState.getIndex())
      .mapToObj(map2Symbol::apply)
      .collect(Collectors.toList());
    return Pair.of(!symbols.isEmpty(), symbols);
  }

  /**
   * whether accepts while emitting an operation from a state to another
   *
   * @param fromState  fromState
   * @param symbolType symbolType
   * @return pair of transitive and next state
   */
  public <T, S extends State> Pair<Boolean, S> accept(S fromState, SymbolType<T> symbolType) {
    return accept(fromState, symbolType, null);
  }

  /**
   * whether accepts while emitting an operation from a state to another
   *
   * @param fromState  fromState
   * @param symbolType symbolType
   * @param p          p
   * @return pair of transitive and next state
   */
  public <T, S extends State> Pair<Boolean, S> accept(S fromState, SymbolType<T> symbolType, Predicate<S> p) {
    Pair<Boolean, Integer> pair = accept(fromState.getIndex(), symbolType.getIndex());
    S nextState = fromState.fetch(pair.getRight());
    return Pair.of((p == null || p.test(nextState)) && pair.getLeft(), nextState);
  }

  /**
   * whether accepts while emitting an operation from a state to another
   *
   * @param fromState fromState
   * @param op        op
   * @return pair of transitive and next state
   */
  public Pair<Boolean, Integer> accept(int fromState, int op) {
    int toState = transTable[fromState][op];
    if (toState == -1) return Pair.of(false, toState);

    return Pair.of(legalStates.contains(toState), toState);
  }

  /**
   * whether accepts the operation sequences
   *
   * @param content content
   * @return whether accepts the sequences
   */
  public boolean accept(String content) {
    return accept(ArrayUtil.asList(content.toCharArray()), CharType::of);
  }

  /**
   * whether accepts the sequences
   *
   * @param content  content
   * @param function function
   * @param <T>      <T>
   * @param <E>      <E>
   * @return whether accepts the sequences
   */
  public <T, E extends Enum<E> & SymbolType<T>> boolean accept(Iterable<T> content, Function<T, Enum<E>> function) {
    int state = 0;
    for (T t : content) {
      int col = function.apply(t).ordinal();
      state = transTable[state][col];
      if (state == -1) return false;
    }

    return legalStates.contains(state);
  }

  /**
   * construct a dfa object
   *
   * @param transTable  transTable
   * @param legalStates legalStates
   * @return dfa object
   */
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

    @Override
    public int getIndex() {
      return ordinal();
    }

    public static CharType of(Integer i) {
      return values()[i];
    }

    public static CharType of(Character c) {
      return SymbolType.of(c, CharType.values());
    }

  }

  /**
   * abstract symbol type
   *
   * @param <T> t
   */
  public interface SymbolType<T> {
    /**
     * @param t t
     * @return whether match
     */
    boolean match(T t);

    /**
     * @return index
     */
    int getIndex();

    /**
     * @param t           t
     * @param symbolTypes symbolTypes
     * @param <E>         <E>
     * @return whether match other
     */
    default <E extends Enum<E> & SymbolType<T>> boolean matchOther(T t, E[] symbolTypes) {
      for (E symbolType : symbolTypes) {
        if (symbolType != this && symbolType.match(t)) {
          return false;
        }
      }
      return true;
    }

    /**
     * @param t           t
     * @param symbolTypes symbolTypes
     * @param <T>         <T>
     * @param <E>         <E>
     * @return SymbolType
     */
    static <T, E extends Enum<E> & SymbolType<T>> E of(T t, E[] symbolTypes) {
      for (E symbolType : symbolTypes) {
        if (symbolType.match(t)) {
          return symbolType;
        }
      }
      throw new IllegalStateException("type of [" + t + "] is not supported");
    }

  }

}
