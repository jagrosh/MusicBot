package com.jagrosh.jmusicbot.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides support for vectorized implementations of filters.
 *
 * <br>By default, only scalar versions are provided.
 */
public class VectorSupport {
    private static final FilterFunctions FUNCTIONS = findImplementation();

    public static void channelMix(float[] left, float[] right, int offset, int length,
                                  float ltl, float ltr, float rtl, float rtr) {
        FUNCTIONS.channelMix(left, right, offset, length, ltl, ltr, rtl, rtr);
    }

    public static double rotation(float[] left, float[] right, int offset, int length,
                                  double x, double dI) {
        return FUNCTIONS.rotation(left, right, offset, length, x, dI);
    }

    public static float tremolo(float[] array, int offset, int length, int sampleRate,
                                float frequency, float depth, float phase) {
        return FUNCTIONS.tremolo(array, offset, length, sampleRate, frequency, depth, phase);
    }

    public static void volume(float[] array, int offset, int length, float volume) {
        FUNCTIONS.volume(array, offset, length, volume);
    }

    private static FilterFunctions findImplementation() {
        List<String> names = new ArrayList<>();
        if(System.getProperty("lavadsp.vectorimpl") != null) {
            names.add(System.getProperty("lavadsp.vectorimpl"));
        }
        names.add(ScalarFunctions.class.getName().replace("ScalarFunctions", "VectorFunctions"));
        for(String s : names) {
            FilterFunctions ff = tryCreating(s);
            if(ff != null) return ff;
        }
        return new ScalarFunctions();
    }

    private static FilterFunctions tryCreating(String className) {
        Class<?> klass;
        try {
            klass = Class.forName(className);
        } catch(ClassNotFoundException e) {
            return null;
        }
        if(!FilterFunctions.class.isAssignableFrom(klass)) {
            throw new IllegalArgumentException("Class " + klass.getName() + " is not a subtype of FilterFunctions");
        }
        try {
            return klass.asSubclass(FilterFunctions.class)
                    .getDeclaredConstructor().newInstance();
        } catch(ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Provides the implementation for the filter methods. Ideally, only a single
     * implementation of this class should be loaded so the JIT can inline the method
     * calls. The default implementations provide scalar versions of the methods, so
     * alternative implementations can only override the methods they can provide
     * better implementations.
     */
    public interface FilterFunctions {
        default void channelMix(float[] left, float[] right, int offset, int length,
                                float ltl, float ltr, float rtl, float rtr) {
            for(int i = 0; i < length; i++) {
                float l = left[offset + i];
                float r = right[offset + i];
                left[offset + i]  = Math.max(-1f, Math.min(1f, ltl * l + rtl * r));
                right[offset + i] = Math.max(-1f, Math.min(1f, ltr * l + rtr * r));
            }
        }

        //returns new `x` value
        default double rotation(float[] left, float[] right, int offset, int length,
                                double x, double dI) {
            for(int i = 0; i < length; i++) {
                //sin(x) and cos(x) return a value in the range [-1, 1], but we want [0, 1], so
                //add one to move to [0, 2] and divide by 2 to obtain [0, 1]
                double sin = Math.sin(x);
                left[offset + i] = left[offset + i] * (float) (sin + 1f) / 2f;
                //cos(x + pi/2) == -sin(x), so just reuse the already computed sine value
                right[offset + i] = right[offset + i] * (float) (-sin + 1f) / 2f;
                x += dI;
            }
            return x;
        }

        //returns new `phase` value
        default float tremolo(float[] array, int offset, int length, int sampleRate,
                              float frequency, float depth, float phase) {
            for(int i = 0; i < length; i++) {
                float fOffset = 1.0f - depth;
                float modSignal = fOffset + depth * (float)Math.sin(phase);
                phase += 2 * Math.PI / sampleRate * frequency;
                array[offset + i] = (modSignal * array[offset + i]);
            }
            return phase;
        }

        default void volume(float[] array, int offset, int length, float volume) {
            for(int i = 0; i < length; i++) {
                array[offset + i] = Math.max(-1f, Math.min(1f, array[offset + i] * volume));
            }
        }
    }
}
