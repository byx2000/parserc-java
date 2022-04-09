package byx.parserc;

/**
 * 函数式接口
 */
public interface FunctionalInterfaces {
    interface Function2<T1, T2, R> {
        R apply(T1 t1, T2 t2);
    }

    interface Function3<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }

    interface Function4<T1, T2, T3, T4, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4);
    }
}
