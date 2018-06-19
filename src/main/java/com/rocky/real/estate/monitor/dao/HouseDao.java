package com.rocky.real.estate.monitor.dao;

import com.rocky.real.estate.monitor.model.HousePo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by rocky on 18/6/18.
 */
@Repository
public interface HouseDao {

    void batchInsert(List<HousePo> pos);

}
