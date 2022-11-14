package com.courage.platform.schedule.worker.rpc;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelUtils {

    private final static Logger logger = LoggerFactory.getLogger(ChannelUtils.class);

    private final static AtomicLong channelId = new AtomicLong(0);

    /**
     * 生成渠道id
     */
    public static Long getChannelId() {
        return channelId.getAndIncrement();
    }

    public static Object getAttr(String key, Channel channel) {
        if (key != null) {
            try {
                AttributeKey<Object> attrKey = AttributeKey.valueOf(key);
                Attribute<Object> attr = channel.attr(attrKey);
                return attr.get();
            } catch (Exception e) {
                logger.error("获取Channel.getAttr失败:", e);
            }
        }
        return null;
    }

    public static void putAttr(String key, String value, Channel channel) {
        if (key != null) {
            try {
                AttributeKey<Object> attrKey = AttributeKey.valueOf(key);
                Attribute<Object> attr = channel.attr(attrKey);
                attr.set(value);
            } catch (Exception e) {
                logger.error("添加Channel.Attribute失败:", e);
            }
        }
    }

    public static void putAttr(String key, List<String> value, Channel channel) {
        if (key != null) {
            try {
                AttributeKey<Object> attrKey = AttributeKey.valueOf(key);
                Attribute<Object> attr = channel.attr(attrKey);
                attr.set(value);
            } catch (Exception e) {
                logger.error("添加Channel. putAttr失败:", e);
            }
        }
    }

}
