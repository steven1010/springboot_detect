package com.skspruce.ism.detect.webapi.strategy.repository.mysqlsecondary;

import com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity.TbArea;
import com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity.TbDpiAppInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface TaJpaRepository extends JpaRepository<TbArea, Long>, CrudRepository<TbArea, Long> {

    @Transactional
    @Query(nativeQuery = true, value = "select id,name  FROM tb_area where id in (:ids)")
    @Modifying
    List<TbArea> findByIds(@Param(value = "ids") Long[] ids);

}
