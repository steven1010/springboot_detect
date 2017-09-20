package com.skspruce.ism.detect.webapi.strategy.repository.mysqlprimary;

import com.skspruce.ism.detect.webapi.strategy.mysqlprimaryentity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface StrategyJpaRepository extends JpaRepository<Strategy, Integer>,CrudRepository<Strategy,Integer> {

    Strategy findStrategyById(Integer id);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM strategy where id in (:ids)")
    @Modifying
    void deleteByIds(@Param(value = "ids") Integer[] ids);
}
