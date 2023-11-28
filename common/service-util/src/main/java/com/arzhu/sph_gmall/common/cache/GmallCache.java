package com.arzhu.sph_gmall.common.cache;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * ClassName: GmallCache
 * Package: com.arzhu.sph_gmall.common.cache
 * Description:
 * 分布式缓存 的 注解
 *  key 前缀 和 后缀 搭配 方法的 参数 生成 redis 的key
 *  例如：   @GmallCache(prefix = "sku",suffix="info")
 *          getSkuInfo(331)
 *          那么生成的 redis 的key 为 sku:331:info
 * @Author arzhu
 * @Create 2023/11/22 21:50
 * @Version 1.0
 */
// 代表着 只能在 方法上起作用
@Target({ElementType.METHOD})
/**
 * 代表着声明周期   可以填的值如下：
 *  1、RetentionPolicy.SOURCE：注解只保留在源文件，当 从 .java 编译成 .class 时 就消失了
 *  2、RetentionPolicy.CLASS：注解被保留到 class 文件中，但是 jvm 编译时 就消失 这是默认的
 *  3、RetentionPolicy.RUNTIME：注解不仅被保存到class文件中，在 jvm 编译之后仍然存在
 */
@Retention(RetentionPolicy.RUNTIME)
/**
 * 继承 是否有效
 * 当添加了 这个注解 那么就代表 子类也会拥有这个注解
 * 比如：class A{} class B extends A {} 假如A 有 这个注解，那么 B也有
 */
@Inherited
// 代表 使用 javadoc 编译时 能显示出 具体的值
@Documented
public @interface GmallCache {
//    key 的前缀
    String prefix() default "key";
//    key的后缀
    String suffix() default "info";
}
