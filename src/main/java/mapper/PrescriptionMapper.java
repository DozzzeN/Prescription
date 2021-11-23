package mapper;

import org.apache.ibatis.annotations.Param;
import pojo.Prescription;

import java.util.List;
import java.util.Map;

public interface PrescriptionMapper {
    Prescription getPrescriptionByPkid(String pkid);

    Prescription getPrescriptionById(String id);

    int getCount();

    List<Map<String, Object>> nameCount();

    List<Map<String, Object>> idCount();

    List<Map<String, Object>> medicineCount();

    List<Map<String, Object>> specificationCount();

    List<Map<String, Object>> unitCount();

    List<Map<String, Object>> priceCount();

    List<Map<String, Object>> numberCount();

    List<Map<String, Object>> amountCount();

    List<String> selectById();

    List<String> selectByName();

    List<String> selectByMedicine();

    List<String> selectBySpecification();

    List<String> selectByUnit();

    List<String> selectByPrice();

    List<String> selectByNumber();

    List<String> selectByAmount();

    int updateByName(@Param("pkid") String pkid, @Param("name") String name);

    int updateByMedicine(@Param("pkid") String pkid, @Param("medicine") String medicine);

    int updateBySpecification(@Param("pkid") String pkid, @Param("specification") String specification);

    int updateByUnit(@Param("pkid") String pkid, @Param("unit") String unit);

    int updateByPrice(@Param("pkid") String pkid, @Param("price") String price);

    int updateByNumber(@Param("pkid") String pkid, @Param("number") String number);

    int updateByAmount(@Param("pkid") String pkid, @Param("amount") String amount);
}
