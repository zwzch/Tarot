package com.zwzch.fool.common;

import com.zwzch.fool.common.exception.CommonExpection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IBase {
    Logger log = LoggerFactory.getLogger(IBase.class);

    /**
     * 检查抛出异常
     * 传入条件为True的值 不抛出异常
     *
     * @param condtion
     * @param e
     * @param logText
     */
    default void checkElseThrow(boolean condtion, CommonExpection e, String logText) {
        if (!condtion) {
            log.error(logText);
            throw e;
        }
    }

    /**
     * 检查抛出异常
     * 传入条件为True的值 不抛出异常
     *
     * @param condtion
     * @param e
     */
    default void checkElseThrow(boolean condtion, CommonExpection e) {
        if (!condtion) {
            throw e;
        }
    }
}
