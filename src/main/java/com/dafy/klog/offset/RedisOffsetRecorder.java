package com.dafy.klog.offset;

import com.dafy.klog.config.OffsetConfig;
import com.dafy.klog.config.RedisConfig;
import com.dafy.klog.offset.OffsetRecorder;
import com.google.common.base.Strings;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Caedmon on 2017/3/2.
 */
public class RedisOffsetRecorder implements OffsetRecorder {
    private JedisPool jedisPool;
    private static final String KLOG_CONSUMER_KEY="klog.consumer.";
    private RedisConfig redisConfig;
    private OffsetConfig offsetConfig;
    public RedisOffsetRecorder(RedisConfig redisConfig,OffsetConfig offsetConfig){
        this.offsetConfig=offsetConfig;
        initJedisPool(redisConfig);
    }
    /**
     * 初始化Redis 连接池
     * */
    private void initJedisPool(RedisConfig redisConfig){
        this.redisConfig=redisConfig;
        JedisPoolConfig poolConfig=redisConfig.jedisPoolConfig;
        String host=redisConfig.host;
        int port=redisConfig.port;
        int database=redisConfig.database;
        this.jedisPool=new JedisPool(poolConfig,host,port);
    }
    @Override
    public void setOffset(long offset) {
        String groupId=offsetConfig.groupId;
        String topicName=offsetConfig.topicName;
        Jedis jedis=jedisPool.getResource();
        String field=topicName+"-"+groupId+"-"+offsetConfig.partition;
        try{
            jedis.hset(KLOG_CONSUMER_KEY,field,String.valueOf(offset));
        }finally {
            jedis.close();
        }
    }

    @Override
    public long getOffset() {
        String groupId=offsetConfig.groupId;
        String topicName=offsetConfig.topicName;
        Jedis jedis=jedisPool.getResource();
        String field=topicName+"-"+groupId+"-"+offsetConfig.partition;
        long offset=0L;
        try{
            String offsetValue=jedis.hget(KLOG_CONSUMER_KEY,field);
            if(!Strings.isNullOrEmpty(offsetValue)){
                offset=Long.valueOf(offsetValue);
            }
        }finally {
            jedis.close();
        }
        return offset;
    }

}
