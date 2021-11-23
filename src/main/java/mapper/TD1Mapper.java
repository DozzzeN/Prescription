package mapper;

public interface TD1Mapper {
    int insertTD1(String td1);

    String selectByLabel(String label_ind);

    int deleteByLabel(String label_ind);

    int deleteTable();
}
