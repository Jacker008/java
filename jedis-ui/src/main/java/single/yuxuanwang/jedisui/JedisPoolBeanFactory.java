package single.yuxuanwang.jedisui;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

public class JedisPoolBeanFactory {

	private ShardedJedisPool jedisPool;

	public ShardedJedisPool getJedisPool() {
		return jedisPool;
	}

	private String hosts;
	private int port;

	@PostConstruct
	private void init() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setTestOnBorrow(true);
		String[] hostArr = hosts.split(",");
		ArrayList<JedisShardInfo> infos = new ArrayList<JedisShardInfo>(hostArr.length);
		for (String host : hostArr) {

			String[] arr = host.split(":");
			infos.add(new JedisShardInfo(arr[0], port, arr[1]));
		}
		jedisPool = new ShardedJedisPool(jedisPoolConfig, infos);
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
