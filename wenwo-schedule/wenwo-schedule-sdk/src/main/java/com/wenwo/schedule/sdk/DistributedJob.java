package com.wenwo.schedule.sdk;

import java.io.Serializable;
import java.util.List;

/**
 * 分片函数
 * 
 * @author yuxuan.wang
 * 
 * @param <T>
 */
public interface DistributedJob<T extends Serializable> extends SimpleJob<T> {

	List<T> slice();
}
