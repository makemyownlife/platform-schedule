package com.courage.platform.schedule.worker.service.recovery;

import com.alibaba.fastjson.JSON;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 恢复数据存储容器
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
        ensureDirOK(baseDir);
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

    public void put(String key, RecoveryMessage recoveryMessage) throws UnsupportedEncodingException, RocksDBException {
        put(key, JSON.toJSONBytes(recoveryMessage));
    }

    public RecoveryMessage get(String key) throws UnsupportedEncodingException, RocksDBException {
        byte[] value = getRawValue(key);
        if (value == null) {
            return null;
        }
        return JSON.parseObject(value, RecoveryMessage.class);
    }

    private void put(String key, byte[] value) throws RocksDBException, UnsupportedEncodingException {
        checkKey(key);
        rocksDB.put(encode(key), value);
    }

    private byte[] getRawValue(String key) throws UnsupportedEncodingException, RocksDBException {
        checkKey(key);
        byte[] value = this.rocksDB.get(encode(key));
        return value;
    }

    public void delete(String key) throws UnsupportedEncodingException, RocksDBException {
        checkKey(key);
        this.rocksDB.delete(encode(key));
    }

    public List<RecoveryMessage> queryList(int count) {
        List<RecoveryMessage> recoveryMessageList = new ArrayList<>(count);
        RocksIterator iterator = this.rocksDB.newIterator();
        iterator.seekToFirst();
        while (iterator.isValid()) {
            byte[] value = iterator.value();
            RecoveryMessage recoveryMessage = JSON.parseObject(value, RecoveryMessage.class);
            recoveryMessageList.add(recoveryMessage);
            iterator.next();
        }
        return recoveryMessageList;
    }

    public void iterator() {
        RocksIterator iterator = this.rocksDB.newIterator();
        iterator.seekToFirst();
        System.out.println(new String(iterator.value()));
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

    public static void ensureDirOK(final String dirName) {
        if (dirName != null) {
            File f = new File(dirName);
            if (!f.exists()) {
                boolean result = f.mkdirs();
                logger.info(dirName + " mkdir " + (result ? "OK" : "Failed"));
            }
        }
    }

}
