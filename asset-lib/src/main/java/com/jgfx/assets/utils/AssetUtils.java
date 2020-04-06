package com.jgfx.assets.utils;

import com.googlecode.gentyref.GenericTypeReflector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class AssetUtils {

    /**
     * Used to obtain the bound value for a generic parameter of a type. Example, for a field of type List&lt;String&gt;, the 0th generic parameter is String.class.
     * A List with no parameter will return Optional.absent()
     *
     * @param target The type to obtain the generic parameter of.
     * @param index  The index of the the parameter to obtain
     * @return An optional that contains the parameter type if bound.
     */
    public static Optional<Type> getTypeParameterBinding(Type target, int index) {
        return getTypeParameterBindingForInheritedClass(target, Objects.requireNonNull(getClassOfType(target)), index);
    }

    /**
     * Used to obtained the bound value for a generic parameter of a particular class or interface that the type inherits.
     *
     * @param target     The type to obtain the generic parameter of.
     * @param superClass The superclass which the parameter belongs to
     * @param index      The index of the parameter to obtain
     * @param <T>        The type of the superclass that the parameter belongs to
     * @return An optional that contains the parameter if bound.
     */
    public static <T> Optional<Type> getTypeParameterBindingForInheritedClass(Type target, Class<T> superClass, int index) {
        if (superClass.getTypeParameters().length == 0) {
            throw new IllegalArgumentException("Class '" + superClass + "' is not parameterized");
        }
        if (!superClass.isAssignableFrom(Objects.requireNonNull(getClassOfType(target)))) {
            throw new IllegalArgumentException("Class '" + target + "' does not implement '" + superClass + "'");
        }

        Type type = GenericTypeReflector.getExactSuperType(target, superClass);
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type paramType = parameterizedType.getActualTypeArguments()[index];
            if (paramType instanceof Class || paramType instanceof ParameterizedType) {
                return Optional.of(paramType);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the raw class of a type, or null if the type doesn't represent a class.
     *
     * @param type The type to get the class of
     * @return the raw class of a type, or null if the type doesn't represent a class.
     */
    public static Class<?> getClassOfType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        }
        return null;
    }
}
