package byx.parserc.exception;

/**
 * Java在创建异常对象时，需要生成调用栈信息，这是个非常耗时的工作
 * 如果使用异常实现控制流跳转，则调用栈的信息不是必须的，关闭这个行为可以极大地提升运行效率
 * 本类在默认构造函数中关闭了异常的调用栈生成，本类的所有子类都不会生成调用栈信息
 */
public class FastException extends RuntimeException {
    public FastException() {
        super(null, null, false, false);
    }
}
