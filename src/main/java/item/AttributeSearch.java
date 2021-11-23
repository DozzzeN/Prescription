package item;

import it.unisa.dia.gas.jpbc.Element;
import net.sf.json.JSONObject;
import util.Helper;

import java.util.*;

import static item.Crypto.*;
import static util.Connection.sMapper;
import static util.Connection.td3Mapper;

public class AttributeSearch {
    private static final List<Element> zeta = new ArrayList<>();
    private static final List<Element> zeta_star = new ArrayList<>();
    private static final List<Element> zeta_cast = new ArrayList<>();
    private static final List<Element> key_omega_at = new ArrayList<>();
    public static Element alpha = pairing.getZr().newRandomElement().getImmutable();
    public static List<String> ind = new ArrayList<>();
    private static List<String> omega;

    public AttributeSearch(byte[] at, int order) {
        step1(at);
        step2();
        step3();
        step4(at, order);
    }

    public static void main(String[] args) {
        byte[] at = "name".getBytes();
        long start = System.currentTimeMillis();
        new AttributeSearch(at,0);
        System.err.println("执行任务消耗了 ：" + (System.currentTimeMillis() - start) + "毫秒");
        System.out.println(ind);
    }

    public static void step1(byte[] at) {
        omega = Helper.getWByAt(new String(at));//包含重复的，但是不包含空值
        for (int i = 0; i < omega.size(); i++) {
            zeta.add(i, h1(mergeByte(at, omega.get(i).getBytes())).mulZn(alpha).getImmutable());
        }
    }

    public static void step2() {
        Element s = pairing.getZr().newElementFromBytes(sMapper.getS("s").getData());
        for (int i = 0; i < omega.size(); i++) {
            zeta_star.add(i, zeta.get(i).mulZn(s).getImmutable());
        }
    }

    public static void step3() {
        for (int i = 0; i < omega.size(); i++) {
            zeta_cast.add(i, zeta_star.get(i).mulZn(alpha.invert()).getImmutable());
            key_omega_at.add(i, h0(zeta_cast.get(i).toBytes()));
        }
    }

    public static void step4(byte[] at, int order) {
        int[] cnt_omega = new int[omega.size()];
        byte[][] label_omega_at = new byte[omega.size()][];
        List<TD3> td3 = new LinkedList<>();
        byte[][] ECounter = new byte[omega.size()][];
        Map<byte[], Integer> map = Helper.formMap(Helper.getLS(new String(at)), new String(at));
        Map<String, Integer> hashmap = Helper.hashMap(map, at);
        for (int i = 0; i < omega.size(); i++) {
            //*1
            String index = h2(mergeByte(at, omega.get(i).getBytes())).getImmutable().toString();
            cnt_omega[i] = hashmap.get(index);
            ECounter[i] = Enc0(key_omega_at.get(i).toBytes(), intToByte(cnt_omega[i]));
            //*2
            cnt_omega[i] = byteToInt(Objects.requireNonNull(
                    Enc1(key_omega_at.get(i).toBytes(), ECounter[i])));
            for (int j = 0; j < cnt_omega[i]; j++) {
                int seed = byteToInt(mergeByte(
                        key_omega_at.get(i).toBytes(), omega.get(i).getBytes(), intToByte(cnt_omega[i] + 1), intToByte(0)));//i从1开始，并且i在add时增加了
                label_omega_at[j] = intToByte(new Random(seed).nextInt());
            }

            if (cnt_omega[i] == 1) {
                StringBuilder label_omega_at_string = new StringBuilder("[");
                for (byte b : label_omega_at[0]) {
                    label_omega_at_string.append(b).append(",");
                }
                label_omega_at_string.deleteCharAt(label_omega_at_string.length() - 1);
                label_omega_at_string.append("]");
                String td3String = td3Mapper.selectOneByLabelOmegaAT(label_omega_at_string.toString());

                JSONObject object = JSONObject.fromObject(td3String);
                td3.add((TD3) JSONObject.toBean(object, TD3.class));
            } else {
                StringBuilder label_omega_at_string = new StringBuilder("[");
                for (byte b : label_omega_at[1]) {
                    label_omega_at_string.append(b).append(",");
                }
                label_omega_at_string.deleteCharAt(label_omega_at_string.length() - 1);
                label_omega_at_string.append("]");
                List<String> td3String = td3Mapper.selectByLabelOmegaAt(label_omega_at_string.toString());
                for (int j = 0; j < cnt_omega[i]; j++) {
                    JSONObject object = JSONObject.fromObject(td3String.get(j));
                    td3.add((TD3) JSONObject.toBean(object, TD3.class));
                }
            }
        }
        td3 = Helper.duplicate(td3);//不包含重复的
        for (int i = 0; i < omega.size(); i++) {
            int seed = byteToInt(mergeByte(
                    key_omega_at.get(i).toBytes(), omega.get(i).getBytes(), intToByte(cnt_omega[i] + 1), intToByte(1)//i从1开始，并且i在add时增加了
            ));
            int a = xor(intToByte(
                    td3.get(i).getData()[order]),
                    td3.get(i).getLabel_at()[order],
                    td3.get(i).getLabel_ind(),
                    intToByte(new Random(seed).nextInt()));//从String转int
//            if (a >= 2069856700) {
//                a -= 2069856648;
//            }
            ind.add(new String(intToByte(a)));
        }
    }
}
