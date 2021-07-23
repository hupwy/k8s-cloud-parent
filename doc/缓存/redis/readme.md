# 1. Redis基本数据类型

最基本也是最常用的数据类型就是 String，set和 get 命令就是 String 的操作命令。 

Redis 的字符串被叫做二进制安全的字符串，为什么是 Binary-safe strings 呢?

下面对于所有的数据类型我们都会从4个维度来分析∶存储类型、操作命令、存储结构、应用场景。

## 1.1 String字符串

### 存储类型

可以用来存储INT（整数）、float（单精度浮点数）、String （字符串）。

### 操作命令

```properties
# 获取指定范围的字符 
getrange test 0 l
# 获取值长度 
strlen test
# 字符串追加内容 
append test good
# 设置多个值（批量操作，原子性） 
mset test1 2673 test2 666
# 获取多个值
mget test2 test1
# 设置值，如果 key 存在，则不成功 
setnx lock 1
# 基于此可实现分布式锁。用 del key 释放锁。
# 但如果释放锁的操作失败了，导致其他节点永远获取不到锁，怎么办?
# 加过期时间。单独用 expire 加过期，也失败了，无法保证原子性，怎么办?多参数 
set key value [expiration EX seconds PX milliseconds][NX|XX]
# 使用参数的方式 
set kl v1 EX 10 NX
#（整数）值递增（值不存在会得到 1） 
incr test 
incrby test 100
# （整数）值递减
decr test1
decrby test1 100
# 浮点数增量 
set mf 2.6
incrbyfloat mf7.3
```

### 存储（实现）原理

- **数据模型**

  Redis的最外层确实是通过hashtable实现的(外层的哈希)，在Redis里面，这个哈希表怎么实现呢?

  我们看一下C语言的源码，每个键值对都是一个dictEntry，通过指针指向key的存储结构和value的存储结构，而且next存储了指向下一个键值对的指针。

  ```c
  typedef struct dictEntry {
      void *key; /*key关键字定义*/ 
      union {
          void *val; /*value定义 */ 
          uint64_t u64; 
          int64_t s64; 
          double d;
      } v;
      struct dictEntry *next; /*指向下一个键值对节点*/
  } dictEntry;
  ```

  实际上最外层是redisDb，redisDb里面放的是dict

  ```c
  typedef struct redisDb {
      dict *dict;                   /* 所有的键值对 *//*The keyspace for this DB*/
      dict *expires;                /* 设置了过期时间的键值对 *//*Timeout of keys with a timeout set*/
      dict *blocking_keys;          /* Keys with clients waiting for data (BLPOP)*/
      dict *ready_keys;             /* Blocked keys that received a PUSH */
      dict *watched_keys;           /* WATCHED keys for MULTI/EXEC CAS */
      int id;                       /* Database ID */
      long long avg_ttl;            /* Average TTL,just for stats */
      unsigned long expires_cursor; /* Cursor of the active expire cycle.*/ 
      list *defrag_later;           /* List of key names to attempt to defrag one by one,gradually.*/
  } redisDb;
  ```

  





