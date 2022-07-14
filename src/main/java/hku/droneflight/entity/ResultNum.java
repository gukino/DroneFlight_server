package hku.droneflight.entity;

/**
 *
 */

public class ResultNum {
    public Integer withMask;
    public Integer withoutMask;
    public Integer unKnown;

    public ResultNum() {
        this.withMask = 0;
        this.withoutMask = 0;
        this.unKnown = 0;
    }

    public ResultNum divide(int divisor){
        this.withMask /= divisor;
        this.withoutMask /= divisor;
        this.unKnown /= divisor;
        return this;
    }

    public ResultNum add(String type, int num){
        switch (type) {
            case "0":
                this.withMask += num;
                break;
            case "1":
                this.withoutMask += num;
                break;
            case "2":
                this.unKnown += num;
                break;
        }
        return this;
    }


    @Override
    public String toString() {
        return "ResultNum{" +
                "withMask=" + withMask +
                ", withoutMask=" + withoutMask +
                ", unKnown=" + unKnown +
                '}';
    }
}
