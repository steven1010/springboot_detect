package com.skspruce.ism.detect.webapi.strategy.repository.mysqlsecondary;

import com.skspruce.ism.detect.webapi.strategy.mysqlprimaryentity.Strategy;
import com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity.TbDpiAppInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TdapJpaRepository extends JpaRepository<TbDpiAppInfo, Integer>, CrudRepository<TbDpiAppInfo, Integer> {

}
