package model;

/**
 * 扑克模型
 * Author: chen
 * DateTime: 1/8/14 9:11 AM
 */
public class Poker {

    private Integer pokerId;

    private Integer value;

    private String fileName;


    public Poker(Integer pokerId, Integer value, String fileName) {
        this.pokerId = pokerId;
        this.value = value;
        this.fileName = fileName;
    }

    /********************************************
     * Getters
     *******************************************/

    public Integer getPokerId() {
        return pokerId;
    }

    public Integer getValue() {
        return value;
    }

    public String getFileName() {
        return fileName;
    }

}
