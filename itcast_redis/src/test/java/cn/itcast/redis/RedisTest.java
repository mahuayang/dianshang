package cn.itcast.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

/**
 *spring/applicationContext-redis.xml
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    //测试字符串
    @Test
    public void testString(){
    redisTemplate.boundValueOps("string_key").set("a");
    Object obj = redisTemplate.boundValueOps("string_key").get();
    System.out.println(obj);
    }

    //测试散列hash
    @Test
        public void testHash(){
        redisTemplate.boundHashOps("hash_key").put("f1","a");
        redisTemplate.boundHashOps("hash_key").put("f2","b");
        redisTemplate.boundHashOps("hash_key").put("f3","c");
        List list = redisTemplate.boundHashOps("hash_key").values();
        System.out.println(list);
    }

    //测试列表list
    @Test
    public void testList(){
        redisTemplate.boundListOps("list_key").leftPush("b");
        redisTemplate.boundListOps("list_key").leftPush("a");
        redisTemplate.boundListOps("list_key").rightPush("c");
        List list_key = redisTemplate.boundListOps("list_key").range(0, -1);
        System.out.println(list_key);
    }

    //测试列表set
    @Test
    public void testSet(){
        redisTemplate.boundSetOps("set_key").add("a","b","c");
        Set set = redisTemplate.boundSetOps("set_key").members();
        System.out.println(set);
    }

    //测试列表sorted set
    @Test
    public void testSortedSet(){
        redisTemplate.boundZSetOps("zset_key").add("b",15);
        redisTemplate.boundZSetOps("zset_key").add("a",5);
        redisTemplate.boundZSetOps("zset_key").add("c",25);
        Set zset_key = redisTemplate.boundZSetOps("zset_key").range(0, -1);
        System.out.println(zset_key);
    }
}
