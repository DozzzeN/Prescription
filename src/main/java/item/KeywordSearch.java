package item;

import it.unisa.dia.gas.jpbc.Element;
import net.sf.json.JSONObject;
import util.Helper;

import java.util.*;

import static item.Crypto.*;
import static util.Connection.sMapper;
import static util.Connection.td3Mapper;

public class KeywordSearch {
    private static final Element kappa = pairing.getZr().newRandomElement().getImmutable();
    public static List<String> ind = new ArrayList<>();
    private static Element pi;
    private static Element pi_star;
    private static Element key_omega_at;
    private static byte[] ECounter;
    private static byte[][] label_omega_at;
    private static int cnt_omega;
    private static TD3[] td3;

    public KeywordSearch(byte[] at, byte[] omega, int order) {
        step1(at, omega);
        step2();
        step3();
        step4(at, omega);
        step5(omega);
        step6();
        step7(omega, order);
    }

    public static void main(String[] args) {
        byte[] at = "medicine".getBytes();
        byte[] omega = "注射用间苯三酚".getBytes();
        long start = System.currentTimeMillis();
        new KeywordSearch(at, omega, 1);
        System.err.println("执行任务消耗了 ：" + (System.currentTimeMillis() - start) + "毫秒");
        System.out.println(ind);

    }

    /**
     * @param at    medicine
     * @param omega 0.9%氯化钠注射液(塑瓶)(甲农铁基)
     */
    public static void step1(byte[] at, byte[] omega) {
        pi = h1(mergeByte(at, omega)).mulZn(kappa).getImmutable();
    }

    public static void step2() {
        Element s = pairing.getZr().newElementFromBytes(sMapper.getS("s").getData());
        pi_star = pi.mulZn(s);
    }

    public static void step3() {
        Element pi_cast = pi_star.mulZn(kappa.invert()).getImmutable();
        key_omega_at = h0(pi_cast.toBytes());
    }

    public static void step4(byte[] at, byte[] omega) {
        String index = h2(mergeByte(at, omega)).getImmutable().toString();
        Map<byte[], Integer> map = Helper.formMap(Helper.getLS(new String(at)), new String(at));
        Map<String, Integer> hashmap = Helper.hashMap(map, at);
        cnt_omega = hashmap.get(index);
        ECounter = Enc0(key_omega_at.toBytes(), intToByte(cnt_omega));
    }

    /**
     * @param omega 搜索单个属性值
     */
    public static void step5(byte[] omega) {
        cnt_omega = byteToInt(Objects.requireNonNull(
                Enc1(key_omega_at.toBytes(), ECounter)));
        label_omega_at = new byte[cnt_omega][];
        for (int i = 0; i < cnt_omega; i++) {
            int seed = byteToInt(mergeByte(
                    key_omega_at.toBytes(), omega, intToByte(cnt_omega + 1), intToByte(0)));//i从1开始，并且i在add时增加了
            label_omega_at[i] = intToByte(new Random(seed).nextInt());
        }
    }

    public static void step6() {
        td3 = new TD3[cnt_omega];
        if (cnt_omega == 1) {
            StringBuilder label_omega_at_string = new StringBuilder("[");
            for (byte b : label_omega_at[0]) {
                label_omega_at_string.append(b).append(",");
            }
            label_omega_at_string.deleteCharAt(label_omega_at_string.length() - 1);
            label_omega_at_string.append("]");
            String td3String = td3Mapper.selectOneByLabelOmegaAT(label_omega_at_string.toString());

            JSONObject object = JSONObject.fromObject(td3String);
            td3[0] = (TD3) JSONObject.toBean(object, TD3.class);
        } else {
            StringBuilder label_omega_at_string = new StringBuilder("[");
            for (byte b : label_omega_at[1]) {
                label_omega_at_string.append(b).append(",");
            }
            label_omega_at_string.deleteCharAt(label_omega_at_string.length() - 1);
            label_omega_at_string.append("]");
            List<String> td3String = td3Mapper.selectByLabelOmegaAt(label_omega_at_string.toString());
            for (int i = 0; i < cnt_omega; i++) {
                JSONObject object = JSONObject.fromObject(td3String.get(i));
                td3[i] = (TD3) JSONObject.toBean(object, TD3.class);
            }
        }
    }

    /**
     * @param omega 查询的属性
     * @param order 是第几个属性：name是0
     */
    public static void step7(byte[] omega, int order) {
        for (int i = 0; i < cnt_omega; i++) {
            int seed = byteToInt(mergeByte(
                    key_omega_at.toBytes(), omega, intToByte(cnt_omega + 1), intToByte(1)//i从1开始，并且i在add时增加了
            ));
            int a = xor(intToByte(
                    td3[i].getData()[order]),
                    td3[i].getLabel_at()[order],
                    td3[i].getLabel_ind(),
                    intToByte(new Random(seed).nextInt()));//从String转int
            ind.add(new String(intToByte(a)));
        }
    }
}
