package com.courage.platform.schedule.server.service.recovery;

import com.courage.platform.schedule.server.service.recovery.delegerfile.DefaultMmapFile;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 延迟存储容器
 * Created by zhangyong on 2019/11/14.
 */
public class RecoveryStore {

    private final static Logger logger = LoggerFactory.getLogger(RecoveryStore.class);

    private final String PLATFORM = "platform";

    private final String SCHEDULE = "schedule";

    private final String baseDir = System.getProperty("user.home") + File.separator + PLATFORM + File.separator + SCHEDULE;

    private volatile boolean inited = false;

    static {
        RocksDB.loadLibrary();
    }

    private RocksDB rocksDB;

    public RecoveryStore() {
        DefaultMmapFile.ensureDirOK(baseDir);
        logger.info("recovery目录:" + baseDir);
    }

    public synchronized void start() throws Exception {
        if (this.inited) {
            return;
        }
        Options options = new Options();
        options.setCreateIfMissing(true);
        rocksDB = RocksDB.open(options, baseDir);
        this.inited = true;
    }

    public void put(String key, byte[] value) throws RocksDBException, UnsupportedEncodingException {
        checkKey(key);
        rocksDB.put(encode(key), value);
    }

    public byte[] get(String key) throws UnsupportedEncodingException, RocksDBException {
        checkKey(key);
        byte[] value = this.rocksDB.get(encode(key));
        return value;
    }

    public void delete(String key) throws UnsupportedEncodingException, RocksDBException {
        checkKey(key);
        this.rocksDB.delete(encode(key));
    }

    private void checkKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key 不能为空");
        }
    }

    private byte[] encode(String key) throws UnsupportedEncodingException {
        return key.getBytes("UTF-8");
    }

    public void shutdown() {
        if (this.rocksDB != null) {
            this.rocksDB.close();
        }
    }

}