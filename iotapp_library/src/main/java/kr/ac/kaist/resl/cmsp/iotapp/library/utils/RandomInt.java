package kr.ac.kaist.resl.cmsp.iotapp.library.utils;

import java.util.Random;

/**
 * Created by shheo on 15. 4. 17.
 */
public class RandomInt {
    private static final Random random = new Random(System.currentTimeMillis());
    private RandomInt() {}
    public static int getNextInt() {
        return random.nextInt();
    }
}
