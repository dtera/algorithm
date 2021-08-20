package cn.cstn.algorithm.commons.state.enums;

import cn.cstn.algorithm.commons.state.State;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * StateEnum
 * @author zhaohuiqiang
 * @date   2020/11/10
 */
@Getter
@RequiredArgsConstructor
public enum StateEnum implements State {
    /**
     * new
     */
    NEW(0, "new"),
    /**
     * draft
     */
    DRAFT(1, "draft"),
    /**
     * audit
     */
    AUDIT(2, "audit"),
    /**
     * running
     */
    RUNNING(3, "running"),
    /**
     * offline
     */
    OFFLINE(4, "offline"),
    /**
     * delete
     */
    DELETE(5, "delete");

    /**
     * index
     */
    private final int index;
    /**
     * desc
     */
    private final String desc;

    @SuppressWarnings("unchecked")
    @Override
    public StateEnum fetch(int state) {
        for (StateEnum stateEnum : values()) {
            if (stateEnum.getIndex() == state)
                return stateEnum;
        }
        throw new IllegalStateException("state of [" + state + "] is not supported");
    }

}
