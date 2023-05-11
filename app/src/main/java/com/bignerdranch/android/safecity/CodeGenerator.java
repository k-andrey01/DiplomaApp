package com.bignerdranch.android.safecity;

import java.util.Random;

public class CodeGenerator {

    public int generateRandomNumber() {
        Random random = new Random();
        int min = 10000;
        int max = 99999;
        return random.nextInt(max - min + 1) + min;
    }

}