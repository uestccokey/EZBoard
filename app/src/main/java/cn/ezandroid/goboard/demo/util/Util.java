package cn.ezandroid.goboard.demo.util;

import java.util.Random;

/**
 * Util
 *
 * @author like
 * @date 2018-01-28
 */
public class Util {

    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }
}
