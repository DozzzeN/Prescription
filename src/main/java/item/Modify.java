package item;

import it.unisa.dia.gas.jpbc.Element;
import net.sf.json.JSONObject;
import util.Helper;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static item.Crypto.*;
import static util.Connection.*;

public class Modify {
    private static final Element b = pairing.getZr().newRandomElement().getImmutable();
    private static Element phi;
    private static Element eta;
    private static Element omega_bar;

    private static Element phi_star;
    private static Element eta_star;
    private static Element omega_bar_star;

    private static Element key_ind;
    private static Element key_at;
    private static Element key_omega_wave_at;

    private static byte[] ECounter;
    private static int cnt_omega_wave;

    private static byte[] label_ind;
    private static byte[] label_at;
    private static byte[] label_omega_wave_at;
    private static int data_wave;

    public Modify(String ind, int order, byte[] at, byte[] omega_wave) {
        step1(ind, at, omega_wave);
        step2();
        step3();
        step4(at, omega_wave);
        step5();
        step6(ind, order, at, omega_wave);
        step7(ind);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        byte[] at = "medicine".getBytes();
        byte[] omega_wave = "0.9%氯化钠注射液(塑瓶)(甲农铁基)".getBytes();
        for (int i = 200; i < 300; i++) {
            new Modify(i + 1 + "", 1, at, omega_wave);
        }
        System.err.println("执行任务消耗了 ：" + (System.currentTimeMillis() - start) + "毫秒");
    }

    public static void step1(String ind, byte[] at, byte[] omega_wave) {
        phi = h1(ind.getBytes()).mulZn(b).getImmutable();
        eta = h1(at).mulZn(b).getImmutable();
        omega_bar = h1(mergeByte(at, omega_wave)).mulZn(b).getImmutable();
    }

    public static void step2() {
        Element s = pairing.getZr().newElementFromBytes(sMapper.getS("s").getData()).getImmutable();
        phi_star = phi.mulZn(s);
        eta_star = eta.mulZn(s);
        omega_bar_star = omega_bar.mulZn(s);
    }

    public static void step3() {
        Element phi_cast = phi_star.mulZn(b.invert()).getImmutable();
        Element eta_cast = eta_star.mulZn(b.invert()).getImmutable();
        Element omega_bar_cast = omega_bar_star.mulZn(b.invert()).getImmutable();
        key_ind = h0(phi_cast.toBytes());
        key_at = h0(eta_cast.toBytes());
        key_omega_wave_at = h0(omega_bar_cast.toBytes());
    }

    public static void step4(byte[] at, byte[] omega_wave) {
        String index = h2(mergeByte(at, omega_wave)).getImmutable().toString();
        Map<byte[], Integer> map = Helper.formMap(Helper.getLS(new String(at)), new String(at));
        Map<String, Integer> hashmap = Helper.hashMap(map, at);
        cnt_omega_wave = hashmap.get(index);
        ECounter = Enc0(key_omega_wave_at.toBytes(), intToByte(cnt_omega_wave));
    }

    public static void step5() {
        cnt_omega_wave = byteToInt(Objects.requireNonNull(
                Enc1(key_omega_wave_at.toBytes(), ECounter)));
        cnt_omega_wave++;
        ECounter = Enc0(key_omega_wave_at.toBytes(), intToByte(cnt_omega_wave));
    }

    public static void step6(String ind, int order, byte[] at, byte[] omega_wave) {
        if (!Helper.updatePrescription(order, ind, new String(omega_wave))) {
            throw new RuntimeException("更新失败");
        } else {
            sqlSession.commit();
        }
        label_ind = H1(key_ind.toBytes(), ind.getBytes());
        label_at = H1(key_at.toBytes(), at);
        int seed = byteToInt(mergeByte(
                key_omega_wave_at.toBytes(), intToByte(cnt_omega_wave), intToByte(0)));
        label_omega_wave_at = intToByte(new Random(seed).nextInt());
        data_wave = xor(ind.getBytes(), label_at, label_ind,
                intToByte(new Random(byteToInt(mergeByte(
                        key_omega_wave_at.toBytes(), omega_wave, intToByte(cnt_omega_wave + 1), intToByte(1))
                )).nextInt()));
    }

    public static void step7(String ind) {
        StringBuilder label_at_string = new StringBuilder("[");
        for (byte b : label_at) {
            label_at_string.append(b).append(",");
        }
        label_at_string.deleteCharAt(label_at_string.length() - 1);
        label_at_string.append("]");
        StringBuilder label_ind_string = new StringBuilder("[");
        for (byte b : label_ind) {
            label_ind_string.append(b).append(",");
        }
        label_ind_string.deleteCharAt(label_ind_string.length() - 1);
        label_ind_string.append("]");

        String td2String = td2Mapper.selectByLabelAtAndLabelInd(label_at_string.toString(), label_ind_string.toString());
        TD2 td2 = (TD2) JSONObject.toBean(JSONObject.fromObject(td2String), TD2.class);

        String td3String = td3Mapper.selectByLabelAtAndLabelInd(label_at_string.toString(), label_ind_string.toString());
        TD3 td3 = (TD3) JSONObject.toBean(JSONObject.fromObject(td3String), TD3.class);

        for (int i = 0; i < td2.getLabel_at().length; i++) {
            if (h0(td2.getLabel_at()[i]).toString().equals(
                    h0(label_omega_wave_at).toString()
            )) {
                td2.getLabel_at()[i] = label_omega_wave_at;
            }
        }

        for (int i = 0; i < td3.getLabel_at().length; i++) {
            if (h0(td3.getLabel_at()[i]).toString().equals(
                    h0(label_omega_wave_at).toString()
            )) {
                td3.getLabel_at()[i] = label_omega_wave_at;
                td3.data[i] = data_wave;
            }
        }

        JSONObject json2 = JSONObject.fromObject(td2);
        String td2Json = json2.toString();

        JSONObject json3 = JSONObject.fromObject(td3);
        String td3Json = json3.toString();

        td2Mapper.updateById(ind, td2Json);
        td3Mapper.updateById(ind, td3Json);

        sqlSession.commit();
    }
}
