package myPackage;

public class Values {
    private long castValue;
    private long baseValue;

    public Values(long base, long cast) {
        baseValue = base;
        castValue = cast;
    }

    public long getCastValue() {
        return castValue;
    }

    public void setCastValue(long castValue) {
        this.castValue = castValue;
    }

    public long getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(long baseValue) {
        this.baseValue = baseValue;
    }
}
