package utils;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 扑克牌相关方法
 * Author: chen
 * DateTime: 1/28/14 2:40 PM
 */
public class Poker {

    private static final Integer POKER_NUM = 52;

    public static List<Integer> init() {
        return init(1);
    }

    public static List<Integer> init( Integer deckNum ) {

        List<Integer> data = Lists.newArrayList();
        for (Integer i = 1; i <= POKER_NUM * deckNum; i++ ) {
            Integer value = (i%52 == 0)  ? 52 : (i%52);
            data.add(value);
        }
        shuffle(data);
        return data;
    }

    private static void shuffle(List<Integer> data) {
        for (int i = 0; i < data.size(); i++) {
            int j = (int) (data.size() * Math.random());
            swap(data, i, j);
        }
    }

    private static void swap(List<Integer> data, int i, int j) {
        if (i == j)  return;

        data.set(i, data.get(i) + data.get(j) );
        data.set(j, data.get(i) - data.get(j) );
        data.set(i, data.get(i) - data.get(j) );
    }

}
