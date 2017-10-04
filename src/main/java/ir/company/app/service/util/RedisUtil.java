package ir.company.app.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.company.app.domain.entity.Challenge;
import ir.company.app.service.dto.GameRedisDTO;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.sf.ehcache.util.Timestamper.next;

/**
 * Created by farzad on 12/13/16.
 */
public class RedisUtil {

    static JedisPool pool;
    static Jedis jedis;

    static {
//        try {
//            String vcap_services = System.getenv("VCAP_SERVICES");
//            if (vcap_services != null && vcap_services.length() > 0) {
        // parsing rediscloud credentials
//                JsonRootNode root = new JdomParser().parse(vcap_services);
//                JsonNode rediscloudNode = root.getNode("rediscloud");
//                JsonNode credentials = rediscloudNode.getNode(0).getNode("credentials");

        pool = new JedisPool(new JedisPoolConfig(), "redis-15875.c8.us-east-1-4.ec2.cloud.redislabs.com", 15875,
            Protocol.DEFAULT_TIMEOUT, "7qLcSQ0ig4svCwkl");
        jedis = pool.getResource();
//            }
//        } catch (InvalidSyntaxException ex) {
//            // vcap_services could not be parsed.
//        }
    }

//        public static void main(String[] args) {
//        jedis.flushDB();
//    }
    public static void addItem(String key, String value) {
        try {


            jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addHashItem(String key, String field, String value) {
        try {


            jedis.hset(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Object getItem(String key) {

        try {
            return new ObjectMapper().readValue(jedis.get(key), Object.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFields(String key, int index) {
        List<String> l = new ArrayList(jedis.hkeys(key));
        if (l.size() > index)
            return l.get(index);
        else return null;
    }

    public static GameRedisDTO getHashItem(String key, String field) {

        try {
            return new ObjectMapper().readValue(jedis.hget(key, field), GameRedisDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getExpressionItem(String pattern) {

        try {
            Set<String> sets = jedis.keys(pattern);
            if (sets == null || sets.size() == 0)
                return null;
            return new ObjectMapper().readValue(jedis.get(sets.iterator().next()), Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void removeItem(String key) {

        jedis.del(key);
    }

    public static void removeItem(String key, String field) {

        jedis.del(key, field);
    }

    public static Long sizeOfMap(String key) {
        return jedis.hlen(key);
    }
//    public static void main(String[] args) {
////        test();
//        Jedis jedis = pool.getResource();
//        Set<String> sets=jedis.keys("3*");
//        for (String set : sets) {
//            jedis.del(
//set
//            );
//        }
//
////        jedis.lrem()
//        String value = jedis.get("omi,");
//// return the instance to the pool when you're done
//        pool.returnResource(jedis);
//    }



}
