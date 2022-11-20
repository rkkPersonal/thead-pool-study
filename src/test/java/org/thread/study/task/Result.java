package org.thread.study.task;

import lombok.Builder;
import lombok.Data;

/**
 * @author Steven
 * @date 2022年11月08日 22:50
 */
@Builder
@Data
public class Result<T> {

    private long costTime;

    private long timeout;

    private long startTime;

    private String remark;

    private T content;


}
