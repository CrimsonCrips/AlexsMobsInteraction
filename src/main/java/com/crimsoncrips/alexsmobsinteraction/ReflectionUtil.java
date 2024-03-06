package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionUtil {
    public static Object createInstance(String className,
                                        Class<?>[] argTypes,
                                        Object[] args) {
        try {
            // Get the class object for the specified class name
            Class<?> clazz = Class.forName(className);

            Constructor<?> constructor = clazz.getDeclaredConstructor(argTypes);

            // Make the constructor accessible
            constructor.setAccessible(true);

            // Create an instance of the class using the constructor
            return constructor.newInstance(args);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setField(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    public static Object getField(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object callMethod(Object object, String name, Class<?>[] argTypes, Object[] args) {
        try {
            java.lang.reflect.Method method = object.getClass().getDeclaredMethod(name, argTypes);
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            return null;
        }
    }
}