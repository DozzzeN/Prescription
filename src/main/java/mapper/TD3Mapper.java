package mapper;

import item.TD3;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TD3Mapper {
    int insertTD3(String td3);

    String selectByLabel(String label_ind);

    int deleteByLabel(String label_ind);

    List<String> selectByLabelOmegaAt(String label_omega_at);

    String selectOneByLabelOmegaAT(String label_omega_at);

    int deleteTable();

    String selectByLabelAtAndLabelInd(@Param("label_at") String label_at, @Param("label_ind") String label_ind);

    int updateById(@Param("id") String id, @Param("td3") String td3);
}
