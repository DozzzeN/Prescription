package item;

import it.unisa.dia.gas.jpbc.Element;
import net.sf.json.JSONObject;

import static item.Crypto.*;
import static util.Connection.*;

public class Deletion {
    private static final Element a = pairing.getZr().newRandomElement().getImmutable();
    private static Element sigma;
    private static Element sigma_star;

    private static byte[] label_ind;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 300; i < 400; i++) {
            new Deletion(i + 1 + "");
        }
        System.err.println("执行任务消耗了 ：" + (System.currentTimeMillis() - start) + "毫秒");
    }

    public Deletion(String ind) {
        step1(ind);
        step2();
        step3(ind);
        step4();
    }

    public static void step1(String ind) {
        sigma = h1(ind.getBytes()).mulZn(a).getImmutable();
    }

    public static void step2() {
        sigma_star = sigma.mulZn(pairing.getZr().newElementFromBytes(sMapper.getS("s").getData())).getImmutable();
    }

    public static void step3(String ind) {
        Element sigma_cast = sigma_star.mulZn(a.invert()).getImmutable();
        Element key_ind = h0(sigma_cast.toBytes());
        label_ind = H1(key_ind.toBytes(), ind.getBytes());
    }

    public static void step4() {
        StringBuilder label_ind_string = new StringBuilder("\"label_ind\":[");
        for (byte b : label_ind) {
            label_ind_string.append(b).append(",");
        }
        label_ind_string.deleteCharAt(label_ind_string.length() - 1);
        label_ind_string.append("]");
        //delete TD

        JSONObject obj1 = JSONObject.fromObject(td1Mapper.selectByLabel(label_ind_string.toString()));
        TD1 td1 = (TD1) JSONObject.toBean(obj1, TD1.class);
        JSONObject obj2 = JSONObject.fromObject(td2Mapper.selectByLabel(label_ind_string.toString()));
        TD2 td2 = (TD2) JSONObject.toBean(obj2, TD2.class);
        JSONObject obj3 = JSONObject.fromObject(td3Mapper.selectByLabel(label_ind_string.toString()));
        TD3 td3 = (TD3) JSONObject.toBean(obj3, TD3.class);

        td1Mapper.deleteByLabel(label_ind_string.toString());
        td2Mapper.deleteByLabel(label_ind_string.toString());
        td3Mapper.deleteByLabel(label_ind_string.toString());

        sqlSession.commit();
    }
}
