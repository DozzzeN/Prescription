package item;

import java.util.Arrays;

public class TD3 {
    public byte[] label_ind;
    public byte[][] label_at;
    public byte[][] label_omega_at;
    public int[] data;

    public TD3() {
    }

    public TD3(byte[] label_ind, byte[][] label_at, byte[][] label_omega_at, int[] data) {
        this.label_ind = label_ind;
        this.label_at = label_at;
        this.label_omega_at = label_omega_at;
        this.data = data;
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

    public byte[][] getLabel_omega_at() {
        return label_omega_at;
    }

    public void setLabel_omega_at(byte[][] label_omega_at) {
        this.label_omega_at = label_omega_at;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TD3{" +
                "label_ind=" + Arrays.toString(label_ind) +
                ", label_at=" + Arrays.toString(label_at) +
                ", label_omega_at=" + Arrays.toString(label_omega_at) +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
