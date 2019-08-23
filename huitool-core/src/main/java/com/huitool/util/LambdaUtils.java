package com.huitool.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <描述信息>
 */
public class LambdaUtils {
    private static final Logger logger = LoggerFactory.getLogger(LambdaUtils.class);
    private static final Pattern INSTANTIATED_METHOD_TYPE = Pattern.compile("\\(L(?<instantiatedMethodType>[\\S&&[^;)]]+);\\)L[\\S]+;");
    private static final Map<Class<?>, SerializedLambda> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Class<?>> LAMBDA_IMPLCLASS_CACHE = new ConcurrentHashMap<>();

    public static String getImplMethodName(Serializable fn){
        return resolve(fn).getImplMethodName();
    }

    public static Class<?> getImplClass(Serializable fn){
        Class<?> klass = LAMBDA_IMPLCLASS_CACHE.get(fn.getClass());

        if(klass == null){
            SerializedLambda lambda = resolve(fn);
            logger.debug("{}", lambda);
            Matcher matcher = INSTANTIATED_METHOD_TYPE.matcher(lambda.getInstantiatedMethodType());
            if(matcher.find()){
                logger.debug("instantiatedMethodType: {}", matcher.group("instantiatedMethodType").replaceAll("/", "."));
                klass = ClassUtils.resolveClassName(matcher.group("instantiatedMethodType").replaceAll("/", "."), null);
                LAMBDA_IMPLCLASS_CACHE.put(fn.getClass(), klass);
            }
        }

        return klass;
    }

    private static SerializedLambda resolve(Serializable fn){
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass());

        if(lambda == null){
            Method method = ReflectionUtils.findMethod(fn.getClass(), "writeReplace");
            method.setAccessible(true);
            lambda = (SerializedLambda) ReflectionUtils.invokeMethod(method, fn);
            CLASS_LAMBDA_CACHE.put(fn.getClass(), lambda);
        }

        return lambda;
    }
}
