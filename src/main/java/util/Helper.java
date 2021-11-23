package util;

import item.TD3;
import pojo.Prescription;

import java.util.*;

import static item.Crypto.*;
import static util.Connection.prescriptionMapper;

public class Helper {
    public static byte[][] getAT(String id) {
        Prescription prescription = prescriptionMapper.getPrescriptionByPkid(id);
        byte[][] AT = new byte[7][];
        if (prescription.getName() != null && prescription.getName().length() != 0) {
            AT[0] = "name".getBytes();
        }
        if (prescription.getMedicine() != null && prescription.getMedicine().length() != 0) {
            AT[1] = "medicine".getBytes();
        }
        if (prescription.getSpecification() != null && prescription.getSpecification().length() != 0) {
            AT[2] = "specification".getBytes();
        }
        if (prescription.getUnit() != null && prescription.getUnit().length() != 0) {
            AT[3] = "unit".getBytes();
        }
        if (prescription.getPrice() != null && prescription.getPrice().length() != 0) {
            AT[4] = "price".getBytes();
        }
        if (prescription.getNumber() != null && prescription.getNumber().length() != 0) {
            AT[5] = "number".getBytes();
        }
        if (prescription.getAmount() != null && prescription.getAmount().length() != 0) {
            AT[6] = "amount".getBytes();
        }
        return AT;
    }

    public static byte[][] getW(String id) {
        Prescription prescription = prescriptionMapper.getPrescriptionByPkid(id);
        byte[][] W = new byte[7][];
        if (prescription.getName() != null && prescription.getName().length() != 0) {
            W[0] = prescription.getName().getBytes();
        }
        if (prescription.getMedicine() != null && prescription.getMedicine().length() != 0) {
            W[1] = prescription.getMedicine().getBytes();
        }
        if (prescription.getSpecification() != null && prescription.getSpecification().length() != 0) {
            W[2] = prescription.getSpecification().getBytes();
        }
        if (prescription.getUnit() != null && prescription.getUnit().length() != 0) {
            W[3] = prescription.getUnit().getBytes();
        }
        if (prescription.getPrice() != null && prescription.getPrice().length() != 0) {
            W[4] = prescription.getPrice().getBytes();
        }
        if (prescription.getNumber() != null && prescription.getNumber().length() != 0) {
            W[5] = prescription.getNumber().getBytes();
        }
        if (prescription.getAmount() != null && prescription.getAmount().length() != 0) {
            W[6] = prescription.getAmount().getBytes();
        }
        return W;
    }

    public static List<String> ATbyteToList(byte[][] bytes) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            result.add(new String(bytes[i]));
        }
        return result;
    }

    public static List<String> WbyteToList(byte[][] bytes) {
        List<String> result = new ArrayList<>();
        result.add(new String(bytes[0]));
        result.add(new String(bytes[1]));
        result.add(new String(bytes[2]));
        result.add(new String(bytes[3]));
        result.add(String.valueOf(bytesToDouble(bytes[4])));
        result.add(String.valueOf(byteToInt(bytes[5])));
        result.add(String.valueOf(byteToInt(bytes[6])));
        return result;
    }

    /**
     * @param input map{number-出现次数, 列名-值}的list
     * @param at    列名
     * @return 值-出现次数的map
     */
    public static Map<byte[], Integer> formMap(List<Map<String, Object>> input, String at) {
        Map<byte[], Integer> resultMap = new HashMap<>();
        for (Map<String, Object> map : input) {
            switch (at) {
                case "id":
                case "name":
                case "medicine":
                case "specification":
                case "unit":
                case "price":
                case "amount":
                case "number":
                    if (map != null && map.size() != 0) {
                        resultMap.put(String.valueOf(map.get(at)).getBytes(),
                                (Long.valueOf(String.valueOf(map.get("number")))).intValue());
                    }
                    break;
                default:
                    break;
            }
        }
        return resultMap;
    }

    /**
     * @param input 值-出现次数的map
     * @param at    列名
     * @return h(列名 | | 值)-出现次数的map
     */
    public static Map<String, Integer> hashMap(Map<byte[], Integer> input, byte[] at) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map.Entry entry : input.entrySet()) {
            resultMap.put(
                    h2(mergeByte(at, (byte[]) entry.getKey())).getImmutable().toString(),
                    (Integer) entry.getValue());
        }
        return resultMap;
    }

    /**
     * @param at 列名
     * @return 列名下所有值进行分组
     */
    public static List<Map<String, Object>> getLS(String at) {
        List<Map<String, Object>> list;
        switch (at) {
            case "id":
                list = prescriptionMapper.idCount();
                break;
            case "name":
                list = prescriptionMapper.nameCount();
                break;
            case "medicine":
                list = prescriptionMapper.medicineCount();
                break;
            case "specification":
                list = prescriptionMapper.specificationCount();
                break;
            case "unit":
                list = prescriptionMapper.unitCount();
                break;
            case "price":
                list = prescriptionMapper.priceCount();
                break;
            case "number":
                list = prescriptionMapper.numberCount();
                break;
            case "amount":
                list = prescriptionMapper.amountCount();
                break;
            default:
                list = null;
                break;
        }
        return list;
    }

    public static boolean compareBytes(byte[] a, byte[] b) {
        if (a == null || b == null) throw new RuntimeException("数组不能为空");
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    public static List<String> getWByAt(String at) {
        List<String> list;
        switch (at) {
            case "id":
                list = prescriptionMapper.selectById();
                break;
            case "name":
                list = prescriptionMapper.selectByName();
                break;
            case "medicine":
                list = prescriptionMapper.selectByMedicine();
                break;
            case "specification":
                list = prescriptionMapper.selectBySpecification();
                break;
            case "unit":
                list = prescriptionMapper.selectByUnit();
                break;
            case "price":
                list = prescriptionMapper.selectByPrice();
                break;
            case "number":
                list = prescriptionMapper.selectByNumber();
                break;
            case "amount":
                list = prescriptionMapper.selectByAmount();
                break;
            default:
                list = null;
                break;
        }
        return list;
    }

    /**
     * 因为相同属性的文件添加后会重复添加TD3
     *
     * @param input 带有重复TD3的list
     * @return 去重后的结果
     */
    public static List<TD3> duplicate(List<TD3> input) {
        List<TD3> output = new LinkedList<>();
        Map<String, TD3> map = new LinkedHashMap<>();
        for (TD3 td3 : input) {
            if (!map.containsKey(h0(td3.getLabel_ind()).toString())) {
                map.put(h0(td3.getLabel_ind()).toString(), td3);
            }
        }
        for (String s : map.keySet()) {
            output.add(map.get(s));
        }
        return output;
    }

    public static boolean updatePrescription(int order, String ind, String newData) {
        switch (order) {
            case 0:
                prescriptionMapper.updateByName(ind, newData);
                break;
            case 1:
                prescriptionMapper.updateByMedicine(ind, newData);
                break;
            case 2:
                prescriptionMapper.updateBySpecification(ind, newData);
                break;
            case 3:
                prescriptionMapper.updateByUnit(ind, newData);
                break;
            case 4:
                prescriptionMapper.updateByPrice(ind, newData);
                break;
            case 5:
                prescriptionMapper.updateByNumber(ind, newData);
                break;
            case 6:
                prescriptionMapper.updateByAmount(ind, newData);
                break;
            default:
                return false;
        }
        return true;
    }
}
