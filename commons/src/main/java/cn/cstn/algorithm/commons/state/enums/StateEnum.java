package cn.cstn.algorithm.commons.state.enums;

import cn.cstn.algorithm.commons.state.State;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * state
 */
@Getter
@RequiredArgsConstructor
public enum StateEnum implements State {
    /**
     * all
     */
    ALL(0, "all"),
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
    DELETE(5, "DELETE");

    /**
     * state
     */
    private final Integer state;
    /**
     * desc
     */
    private final String desc;

    @SuppressWarnings("unchecked")
    @Override
    public StateEnum of(int state) {
        for (StateEnum stateEnum : values()) {
            if (stateEnum.getState() == state)
                return stateEnum;
        }
        throw new IllegalStateException("state of [" + state + "] is not supported");
    }

}
