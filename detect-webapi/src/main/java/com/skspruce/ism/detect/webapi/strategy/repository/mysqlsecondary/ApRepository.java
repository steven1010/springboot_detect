package com.skspruce.ism.detect.webapi.strategy.repository.mysqlsecondary;



import com.skspruce.ism.detect.webapi.strategy.mysqlsecondaryentity.ApEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ApRepository extends JpaRepository<ApEntity, Long> {
    //by ap
    String apsql = "select m.id as ap_id,m.mac,m.ap_name,m.remark, n.area_id ,p.name as area_name,r.location from tb_ap m " +
            "left join  tb_area_ap as n on m.id = n.ap_id  " +
            "left join tb_area as p on n.area_id = p.id " +
            "left join tb_area_place as r on p.id = r.id  ";
    //by area place
    String placeSql = "select c.ap_id ,d.mac,d.ap_name,d.remark,a.id as area_id,b.name as area_name,a.location  from tb_area_place as a  " +
            "left join tb_area as b on a.id=b.id " +
            "left join tb_area_ap as c on b.id=c.area_id " +
            "left join tb_ap as d on c.ap_id=d.id ";

    @Transactional
    @Modifying
    @Query(value = apsql + " where m.mac like %:mac%", nativeQuery = true)
    List<Object[]> findByMac(@Param("mac") String mac);

    @Transactional
    @Modifying
    @Query(value = apsql + " where m.ap_name like %:apName%", nativeQuery = true)
    List<Object[]> findByApName(@Param("apName") String apName);

    @Transactional
    @Modifying
    @Query(value = apsql + " where m.mac like %:mac% and m.ap_name like %:apName%", nativeQuery = true)
    List<Object[]> findByMacAndApName(@Param("mac") String mac, @Param("apName") String apName);

    @Transactional
    @Modifying
    @Query(value = placeSql + " where  a.address like %:address% ", nativeQuery = true)
    List<Object[]> findByAddress(@Param("address") String address);

    @Transactional
    @Modifying
    @Query(value = placeSql + " where a.no like %:no% ", nativeQuery = true)
    List<Object[]> findByNo(@Param("no") String no);

    @Transactional
    @Modifying
    @Query(value = placeSql + " where a.address like %:address% and a.no like %:no% ", nativeQuery = true)
    List<Object[]> findByAddressAndNo(@Param("address") String address, @Param("no") String no);
}
