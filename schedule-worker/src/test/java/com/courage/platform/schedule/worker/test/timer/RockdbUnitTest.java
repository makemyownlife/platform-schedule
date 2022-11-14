package com.courage.platform.schedule.worker.test.timer;

import org.junit.Before;
import org.junit.Test;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.UnsupportedEncodingException;

public class RockdbUnitTest {

    static {
        RocksDB.loadLibrary();
    }

    private RocksDB rocksDB;

    @Before
    public void before() throws RocksDBException {
        Options options = new Options();
        options.setCreateIfMissing(true);
        this.rocksDB = RocksDB.open(options, "D:\\test");
    }

    @Test
    public void scan() throws UnsupportedEncodingException, RocksDBException {
        this.rocksDB.put("hello".getBytes(), "张勇".getBytes("UTF-8"));
        byte[] bytes = this.rocksDB.get("hello".getBytes());
        String value = new String(bytes, "UTF-8");
        System.out.println(value);
    }

}
