package com.courage.platform.schedule.dao;

import com.courage.platform.schedule.dao.domain.AppInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface AppinfoDao {

    AppInfo findAppinfoByAppKey(@Param("appKey") String appKey);

    AppInfo findAppinfoById(@Param("id") String id);

    List<AppInfo> findAll();

    int insertAppInfo(AppInfo appInfo);

    int updateAppInfo(AppInfo appInfo);

    int update2(AppInfo appInfo);

    void deleteAppInfoByIds(List<String> appIds);

    List<AppInfo> findPage(Map map);

    Integer count(Map map);

    Integer getMaxAppKey();

}
