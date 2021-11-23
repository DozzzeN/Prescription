package mapper;

import org.apache.ibatis.annotations.Param;

public interface TD2Mapper {
    int insertTD2(String td2);

    String selectByLabel(String label_ind);

    int deleteByLabel(String label_ind);

    int deleteTable();

    String selectByLabelAtAndLabelInd(@Param("label_at") String label_at, @Param("label_ind") String label_ind);

    int updateById(@Param("id") String id, @Param("td2") String td2);
}
