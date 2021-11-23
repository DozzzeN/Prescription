package item;

import it.unisa.dia.gas.jpbc.Element;
import net.sf.json.JSONObject;
import util.Helper;

import java.util.*;
import java.util.concurrent.*;

import static item.Crypto.*;
import static util.Connection.*;

public class Addition {
    private static final Element r = pairing.getZr().newRandomElement().getImmutable();
    private static final Element s = pairing.getZr().newRandomElement().getImmutable();
    private static final List<Element> omega_star = new ArrayList<>();
    private static final List<Element> key_at = new ArrayList<>();
    private static final List<Element> key_omega_at = new ArrayList<>();
    private static final List<Element> sigma_star = new ArrayList<>();
    private static final List<Element> sigma = new ArrayList<>();
    private static final List<Element> omega = new ArrayList<>();
    public static byte[] label_ind;
    public static byte[][] label_at;
    public static int[] data;
    private static int fileCounts = prescriptionMapper.getCount();
    private static Element delta;
    private static int n;
    private static byte[][] w;
    private static byte[][] at;
    private static int[] cnt_omega;
    private static byte[][] label_omega_at;
    private static Element delta_star;
    private static Element key_ind;
    private static byte[][] ECounter;

    public Addition() {
        td1Mapper.deleteTable();
        td2Mapper.deleteTable();
        td3Mapper.deleteTable();
        sMapper.deleteTable();
        sMapper.insertS(new S("s", s.toBytes()));
        sqlSession.commit();
        for (int i = 0; i < fileCounts; i++) {
            process(i);
        }
    }

    public static void process(int i) {
        step1(String.valueOf(i + 1));
        step2();
        step3();
        step4();
        step5();
        step6(String.valueOf(i + 1));
        step7();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        new Addition();
        System.err.println("执行任务消耗了 ：" + (System.currentTimeMillis() - start) + "毫秒");
    }

    public static void main1(String[] args) throws InterruptedException, ExecutionException {
        td1Mapper.deleteTable();
        td2Mapper.deleteTable();
        td3Mapper.deleteTable();
        sMapper.deleteTable();
        sMapper.insertS(new S("s", s.toBytes()));
        sqlSession.commit();
        long start = System.currentTimeMillis();
        int threadSize = fileCounts / 8;
        int dataSize = fileCounts;
        int threadNum = dataSize / threadSize + 1;
        // 定义标记,过滤threadNum为整数
        boolean special = dataSize % threadSize == 0;
        // 创建一个线程池
        ExecutorService exec = Executors.newFixedThreadPool(threadNum);
        // 定义一个任务集合
        List<Callable<String>> tasks = new ArrayList<>();
        Callable<String> task;
        int left, right;
        // 确定每条线程的数据
        for (int i = 0; i < threadNum; i++) {
            if (i == threadNum - 1) {
                if (special) {
                    break;
                }
                left = threadSize * i;
                right = dataSize;
            } else {
                left = threadSize * i;
                right = threadSize * (i + 1);
            }
            int finalLeft = left;
            int finalRight = right;
            task = () -> {
                for (int j = finalLeft; j < finalRight; j++) {
                    process(j);
                }
                return "ok";
            };// 这里提交的任务容器列表和返回的Future列表存在顺序对应的关系
            tasks.add(task);
        }
        List<Future<String>> results = exec.invokeAll(tasks);
        for (Future<String> future : results) {
            System.out.println(future.get());
        }
        // 关闭线程池
        exec.shutdown();
        System.out.println("线程任务执行结束");
        System.err.println("执行任务消耗了 ：" + (System.currentTimeMillis() - start) + "毫秒");
    }

    /**
     * @param ind 索引
     */
    public static void step1(String ind) {
        //根据id查找数据库
        w = Helper.getW(ind);
        at = Helper.getAT(ind);
        n = w.length;
        delta = h1(ind.getBytes()).mulZn(r).getImmutable();
        for (int i = 0; i < n; i++) {
            sigma.add(i, h1(at[i]).mulZn(r).getImmutable());
            omega.add(i, h1(mergeByte(at[i], w[i])).mulZn(r).getImmutable());
        }
    }

    public static void step2() {
        delta_star = delta.mulZn(s).getImmutable();
        for (int i = 0; i < n; i++) {
            sigma_star.add(i, sigma.get(i).mulZn(s).getImmutable());
            omega_star.add(i, omega.get(i).mulZn(s).getImmutable());
        }
    }

    public static void step3() {
        Element delta_cast = delta_star.mulZn(r.invert()).getImmutable();
        List<Element> sigma_cast = new ArrayList<>();
        List<Element> omega_cast = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            sigma_cast.add(i, sigma_star.get(i).mulZn(r.invert()).getImmutable());
            omega_cast.add(i, omega_star.get(i).mulZn(r.invert()).getImmutable());
        }

        key_ind = h0(delta_cast.toBytes());
        for (int i = 0; i < n; i++) {
            key_at.add(i, h0(sigma_cast.get(i).toBytes()));
            key_omega_at.add(i, h0(omega_cast.get(i).toBytes()));
        }
    }

    public static void step4() {
        ECounter = new byte[n][];
        cnt_omega = new int[n];
        List<String> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            indices.add(i, h2(mergeByte(at[i], w[i])).getImmutable().toString());
        }
        //数据处理
        for (int i = 0; i < n; i++) {
            Map<byte[], Integer> map = Helper.formMap(Helper.getLS(new String(at[i])), new String(at[i]));
            Map<String, Integer> hashmap = Helper.hashMap(map, at[i]);
            cnt_omega[i] = hashmap.get(indices.get(i));
        }
        for (int i = 0; i < n; i++) {
            ECounter[i] = Enc0(key_omega_at.get(i).toBytes(), intToByte(cnt_omega[i]));
        }
    }

    public static void step5() {
        for (int i = 0; i < n; i++) {
            cnt_omega[i] = byteToInt(Objects.requireNonNull(Enc1(key_omega_at.get(i).toBytes(), ECounter[i])));
            cnt_omega[i]++;
            ECounter[i] = Enc0(key_omega_at.get(i).toBytes(), intToByte(cnt_omega[i]));
        }
    }

    public static void step6(String ind) {
        label_at = new byte[n][];
        label_omega_at = new byte[n][];
        data = new int[n];
        label_ind = H1(key_ind.toBytes(), ind.getBytes());
        for (int i = 0; i < n; i++) {
            label_at[i] = H1(key_at.get(i).toBytes(), at[i]);
            int seed0 = byteToInt(mergeByte(
                    key_omega_at.get(i).toBytes(), w[i], intToByte(cnt_omega[i]), intToByte(0)));
            label_omega_at[i] = intToByte(new Random(seed0).nextInt());
            int seed1 = byteToInt(mergeByte(
                    key_omega_at.get(i).toBytes(), w[i], intToByte(cnt_omega[i]), intToByte(1)));
            data[i] = xor(ind.getBytes(), label_at[i], label_ind, intToByte(new Random(seed1).nextInt()));
        }
    }

    public static void step7() {
        TD1 td1 = new TD1(label_ind, label_at);
        JSONObject json1 = JSONObject.fromObject(td1);
        String td1Json = json1.toString();

        TD2 td2 = new TD2(label_ind, label_at, label_omega_at);
        JSONObject json2 = JSONObject.fromObject(td2);
        String td2Json = json2.toString();

        TD3 td3 = new TD3(label_ind, label_at, label_omega_at, data);
        JSONObject json3 = JSONObject.fromObject(td3);
        String td3Json = json3.toString();

        td1Mapper.insertTD1(td1Json);
        td2Mapper.insertTD2(td2Json);
        td3Mapper.insertTD3(td3Json);

        sqlSession.commit();
    }
}
