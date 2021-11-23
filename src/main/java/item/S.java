package item;

import java.util.Arrays;

public class S {
    private String name;
    private byte[] data;

    public S() {
    }

    public S(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "s{" +
                "name='" + name + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
