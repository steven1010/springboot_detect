package com.skspruce.ism.detect.webapi.strategy.repo;

import com.skspruce.ism.detect.webapi.strategy.entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface StrategyRepository extends JpaRepository<Strategy, Integer> {

    Strategy findStrategyById(Integer id);

    @Transactional
    @Query(value = "delete from strategy where id in(?1)", nativeQuery = true)
    @Modifying
    void deleteByIds(String ids);
}
