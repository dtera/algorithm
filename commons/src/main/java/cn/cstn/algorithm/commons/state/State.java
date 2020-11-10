package cn.cstn.algorithm.commons.state;

/**
 * State
 *
 * @author zhaohuiqiang
 * @date 2020/11/3
 */
public interface State {

    /**
     * get the state index in dfa transition table {@link cn.cstn.algorithm.commons.graph.DFA}
     *
     * @return the index
     */
    int getIndex();

    /**
     * fetch state enum by index {@link cn.cstn.algorithm.commons.state.enums.StateEnum}
     *
     * @param index state index in transition table
     * @param <S> the type
     * @return state enum
     */
    <S extends State> S fetch(int index);

}
