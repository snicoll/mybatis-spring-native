/*
 *    Copyright 2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.spring.nativex;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ibatis.reflection.TypeParameterResolver;

public class MyBatisMapperTypeUtils {

  private MyBatisMapperTypeUtils() {
    // NOP
  }

  static Class<?> resolveReturnClass(Class<?> mapperInterface, Method method) {
    Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
    return typeToClass(resolvedReturnType, method.getReturnType());
  }

  static List<Class<?>> resolveParameterClasses(Class<?> mapperInterface, Method method) {
    return Stream.of(TypeParameterResolver.resolveParamTypes(method, mapperInterface))
        .map(x -> typeToClass(x, Object.class)).collect(Collectors.toList());
  }

  // TODO Support complex pattern and nested type
  private static Class<?> typeToClass(Type src, Class<?> fallback) {
    Class<?> result = null;
    if (src instanceof Class<?>) {
      if (((Class<?>) src).isArray()) {
        result = ((Class<?>) src).getComponentType();
      } else {
        result = (Class<?>) src;
      }
    } else if (src instanceof ParameterizedType) {
      Type actualType = ((ParameterizedType) src).getActualTypeArguments()[0];
      result = typeToClass(actualType, fallback);
    }
    if (result == null) {
      result = fallback;
    }
    return result;
  }

}
