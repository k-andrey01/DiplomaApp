package com.bignerdranch.android.safecity.HelperClass;

import java.util.Random;

public class CodeGenerator {

    public String generateRandomNumber() {
        Random random = new Random();
        int min = 10000;
        int max = 99999;
        return String.valueOf(random.nextInt(max - min + 1) + min);
    }

}