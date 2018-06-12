package com.rocky.dao;

import com.rocky.model.PersonPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by rocky on 16/9/2.
 */
@Repository
public interface IHelloWorldDao {
    PersonPo getById(@Param("id") Long id);
    void insert(PersonPo personPo);
    void batchInsert(List<PersonPo> personPoList);
}
