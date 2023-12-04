package com.github.laefye.fishing;

import java.util.ArrayList;
import java.util.Random;

public class Randomizer {
    private ArrayList<Integer> numbers = new ArrayList<>();

    public void add(int x) {
        numbers.add(x);
    }

    private int sum(int max) {
        int sum = 0;
        for (int i = 0; i <= max; i++) {
            sum += numbers.get(i);
        }
        return sum;
    }

    private int sum() {
        return sum(numbers.size() - 1);
    }

    public int random(Random random) {
        int x = random.nextInt(sum());
        for (int i = 0; i < numbers.size(); i++) {
            if (x < sum(i)) {
                return i;
            }
        }
        return -1;
    }
}
