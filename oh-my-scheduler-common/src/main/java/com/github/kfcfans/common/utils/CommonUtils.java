package com.github.kfcfans.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;


/**
 * 公共工具类
 *
 * @author tjq
 * @since 2020/3/18
 */
@Slf4j
public class CommonUtils {

    /**
     * 重试执行，仅适用于失败抛出异常的方法
     * @param executor 需要执行的方法
     * @param retryTimes 重试的次数
     * @param intervalMS 失败后下一次执行的间隔时间
     * @return 函数成功执行后的返回值
     * @throws Exception 执行失败，调用方自行处理
     */
    public static <T> T executeWithRetry(SupplierPlus<T> executor, int retryTimes, long intervalMS) throws Exception {
        if (retryTimes <= 1 || intervalMS <= 0) {
            return executor.get();
        }
        for (int i = 1; i < retryTimes; i++) {
            try {
                return executor.get();
            }catch (Exception e) {
                log.warn("[CommonUtils] executeWithRetry failed, system will retry after {}ms.", intervalMS, e);
                Thread.sleep(intervalMS);
            }
        }
        return executor.get();
    }

    /**
     * 重试执行，仅适用于根据返回值决定是否执行成功的方法
     * @param booleanExecutor 需要执行的方法，其返回值决定了执行是否成功
     * @param retryTimes 重试次数
     * @param intervalMS 失败后下一次执行的间隔时间
     * @return 最终执行结果
     */
    public static boolean executeWithRetryV2(Supplier<Boolean> booleanExecutor, int retryTimes, long intervalMS) {

        if (retryTimes <= 1 || intervalMS <= 0) {
            return booleanExecutor.get();
        }

        for (int i = 0; i < retryTimes; i++) {
            try {
                if (booleanExecutor.get()) {
                    return true;
                }
                Thread.sleep(intervalMS);
            }catch (Exception ignore) {
            }
        }
        return booleanExecutor.get();
    }
}