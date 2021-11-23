package item;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static item.Crypto.*;
import static util.Connection.sMapper;

public class ConditionSearch {
    private static final Element beta = pairing.getZr().newRandomElement().getImmutable();
    private static final List<Element> phi = new ArrayList<>();
    private static final List<Element> phi_star = new ArrayList<>();
    private static final List<Element> key_omega_cast_at_cast = new ArrayList<>();
    private static Element epsilon;
    private static Element theta;
    private static int n;
    private static Element epsilon_star;
    private static Element theta_star;
    private static Element key_omega_at;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        byte[] at_cast = "name".getBytes();
        new AttributeSearch(at_cast, 0);

        byte[] at = "medicine".getBytes();
        byte[] omega = "0.9%氯化钠注射液(塑瓶)(甲农铁基)".getBytes();
        new KeywordSearch(at, omega, 1);
        AttributeSearch.ind.retainAll(KeywordSearch.ind);
        System.err.println("执行任务消耗了 ：" + (System.currentTimeMillis() - start) + "毫秒");
        System.out.println(AttributeSearch.ind);
    }

    public static void step1(byte[] at, byte[] omega, byte[] at_cast, byte[][] omega_cast) {
        n = omega_cast.length;
        epsilon = h1(mergeByte(at, omega)).mulZn(beta).getImmutable();
        theta = h1(at_cast).mulZn(beta).getImmutable();
        for (int i = 0; i < n; i++) {
            phi.add(i, h1(mergeByte(at_cast, omega_cast[i])).mulZn(beta).getImmutable());
        }
    }

    public static void step2() {
        Element s = pairing.getZr().newElementFromBytes(sMapper.getS("s").getData());

        epsilon_star = epsilon.mulZn(s);
        theta_star = theta.mulZn(s);
        for (int i = 0; i < n; i++) {
            phi_star.add(i, phi.get(i).mulZn(s));
        }
    }

    public static void step3() {
        Element epsilon_cast = epsilon_star.mulZn(beta.invert()).getImmutable();
        Element theta_cast = theta_star.mulZn(beta.invert()).getImmutable();
        List<Element> phi_cast = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            phi_cast.add(i, phi_star.get(i).mulZn(beta.invert()).getImmutable());
            key_omega_cast_at_cast.add(i, h0(phi_cast.get(i).toBytes()));
        }
        key_omega_at = h0(epsilon_cast.toBytes());
        Element key_at_cast = h0(theta_cast.toBytes());
    }

    public static void step4(byte[] at, byte[] omega, byte[] at_cast, byte[] omega_cast) {
        Element index1 = h2(mergeByte(at, omega));
        Element index2 = h2(mergeByte(at_cast, omega_cast));
        int cnt_omega = 0;//database
        int cnt_omega_cast = 0;//database
        byte[] ECounter_omega = Enc0(key_omega_at.toBytes(), intToByte(cnt_omega));
        byte[] ECounter_omega_cast = Enc0(key_omega_at.toBytes(), intToByte(cnt_omega_cast));

        cnt_omega = byteToInt(Objects.requireNonNull(Enc1(key_omega_at.toBytes(), ECounter_omega)));
        cnt_omega_cast = byteToInt(Objects.requireNonNull(Enc1(key_omega_at.toBytes(), ECounter_omega_cast)));

        byte[][] label_omega_at = new byte[cnt_omega][];
        byte[][] label_omega_cast_at_cast = new byte[cnt_omega_cast][];

        for (int i = 0; i < cnt_omega; i++) {
            label_omega_at[i] = intToByte(new Random(byteToInt(mergeByte(
                    key_omega_at.toBytes(), omega, intToByte(i), intToByte(0))
            )).nextInt());
        }

        for (int i = 0; i < cnt_omega_cast; i++) {
            label_omega_cast_at_cast[i] = intToByte(new Random(byteToInt(mergeByte(
                    key_omega_cast_at_cast.get(i).toBytes(), omega_cast, intToByte(i), intToByte(0))
            )).nextInt());
        }
    }
}
