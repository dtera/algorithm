package cn.cstn.algorithm.commons.state;

/**
 * State
 *
 * @author zhaohuiqiang
 * @date 2020/11/3
 */
public interface State {

    /**
     * return state
     *
     * @return state
     */
    int getState();

    /**
     * state
     *
     * @param state state
     * @param <S>   <S>
     * @return state
     */
    <S extends State> S of(int state);

}
