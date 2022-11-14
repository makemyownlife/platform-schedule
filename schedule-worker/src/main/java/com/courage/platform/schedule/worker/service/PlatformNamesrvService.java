package com.courage.platform.schedule.worker.service;

import com.courage.platform.schedule.dao.PlatformNamesrvDao;
import com.courage.platform.schedule.dao.domain.PlatformNamesrv;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhangyong on 2019/11/4.
 */
@Service
public class PlatformNamesrvService {

    private final static Logger logger = LoggerFactory.getLogger(PlatformNamesrvService.class);

    @Autowired
    private PlatformNamesrvDao platformNamesrvDao;

    public PlatformNamesrv getPlatformNamesrvByNamesrvIp(String namesrvIp) {
        return platformNamesrvDao.getPlatformNamesrvByNamesrvIp(namesrvIp);
    }

    public List<PlatformNamesrv> findAll() {
        return platformNamesrvDao.findAll();
    }

    public boolean isValidHost(String hostIp) {
        List<PlatformNamesrv> platformNamesrvList = platformNamesrvDao.findAll();
        if (CollectionUtils.isNotEmpty(platformNamesrvList)) {
            for (PlatformNamesrv platformNamesrv : platformNamesrvList) {
                if (hostIp.equals(platformNamesrv.getNamesrvIp())) {
                    return true;
                }
            }
        }
        logger.warn("currentHost:" + hostIp + " isnot available , you should deploy to another container!");
        return false;
    }

}
