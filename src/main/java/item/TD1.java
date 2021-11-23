package item;

import java.util.Arrays;

public class TD1 {
    private byte[] label_ind;
    private byte[][] label_at;

    public TD1() {
    }

    public TD1(byte[] label_ind, byte[][] label_at) {
        this.label_ind = label_ind;
        this.label_at = label_at;
    }

    public byte[] getLabel_ind() {
        return label_ind;
    }

    public void setLabel_ind(byte[] label_ind) {
        this.label_ind = label_ind;
    }

    public byte[][] getLabel_at() {
        return label_at;
    }

    public void setLabel_at(byte[][] label_at) {
        this.label_at = label_at;
    }

    @Override
    public String toString() {
        return "TD1{" +
                "label_ind=" + Arrays.toString(label_ind) +
                ", label_at=" + Arrays.toString(label_at) +
                '}';
    }
}
