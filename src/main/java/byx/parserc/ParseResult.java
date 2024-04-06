package byx.parserc;

/**
 * 封装解析结果
 * @param result 解析结果
 * @param index 解析后的索引
 * @param <R> 结果类型
 */
public record ParseResult<R>(R result, int index) {}
