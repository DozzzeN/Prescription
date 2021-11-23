package util;

import mapper.*;
import org.apache.ibatis.session.SqlSession;

import java.util.Map;

import static item.Crypto.byteToInt;

public class Connection {
    public static SqlSession sqlSession = SqlSessionFactoryUtils.openSqlSession();
    public static PrescriptionMapper prescriptionMapper = sqlSession.getMapper(PrescriptionMapper.class);
    public static TD1Mapper td1Mapper = sqlSession.getMapper(TD1Mapper.class);
    public static TD2Mapper td2Mapper = sqlSession.getMapper(TD2Mapper.class);
    public static TD3Mapper td3Mapper = sqlSession.getMapper(TD3Mapper.class);
    public static SMapper sMapper = sqlSession.getMapper(SMapper.class);

    public static void main(String[] args) {
//        byte[][] a = Helper.getW(100);
//        byte[][] b = Helper.getAT(100);
//        for (int i = 0; i < 7; i++) {
//            System.out.println(Arrays.toString(a[i]));
//            System.out.println(Arrays.toString(b[i]));
//        }
//        System.out.println(Helper.ATbyteToList(b));
//        System.out.println(Helper.WbyteToList(a));
        Map<byte[], Integer> map = Helper.formMap(Helper.getLS("number"), "number");
        for (byte[] bytes : map.keySet()) {
            System.out.println(byteToInt(bytes));
        }
    }
}
