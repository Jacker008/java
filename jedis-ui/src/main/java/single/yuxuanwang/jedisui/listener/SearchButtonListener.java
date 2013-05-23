package single.yuxuanwang.jedisui.listener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import single.yuxuanwang.jedisui.ComponentRegedit;
import single.yuxuanwang.jedisui.JedisPoolBeanFactory;

/**
 * 监听用户点击“搜索”按钮
 * 
 * @author wangyuxuan
 * 
 */
public class SearchButtonListener implements ActionListener {

	@Autowired
	private JedisPoolBeanFactory jedisPoolBeanFactory;

	public void actionPerformed(ActionEvent e) {
		JTextField searchKey = ComponentRegedit.get("searchKey");
		JPanel dataView = ComponentRegedit.get("dataView");
		String key = searchKey.getText();
		if (StringUtils.isNotBlank(key)) {
			ShardedJedisPool jedisPool = jedisPoolBeanFactory.getJedisPool();
			ShardedJedis resource = null;
			try {
				resource = jedisPool.getResource();
				dataView.removeAll();
				if (resource.exists(key)) {
					String type = resource.type(key);
					String value = getByType(resource, key, type);
					Jedis jedis = resource.getShard(key);
					SearchResult result = new SearchResult(key, value, type, jedis.getClient().getHost());
					showData(result);
				}
				dataView.validate();
				dataView.repaint();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (resource != null)
					jedisPool.returnResource(resource);
			}
		}
	}

	/**
	 * 通过存储类型获取对应的值
	 * 
	 * @param resource
	 * @param key
	 * @param type
	 * @return
	 */
	private String getByType(ShardedJedis resource, String key, String type) {
		if ("string".equals(type))
			return resource.get(key);
		if ("list".equals(type))
			return format(resource.lrange(key, 0, resource.llen(key)));
		if ("set".equals(type))
			return format(resource.smembers(key));
		if ("zset".equals(type))
			return format(resource.zrange(key, 0, resource.zcard(key)));
		if ("hash".equals(type))
			return resource.hgetAll(key).toString();

		return null;
	}

	private String format(Collection<String> coll) {
		// return coll.toString();
		return StringUtils.join(coll, "<br>");
	}

	private static final String INITIAL_TEXT = 
								"KEY: %s\n" +
								"VALUE: %s\n" +
								"TYPE: %s\n" +
								"LOCATION: %s\n";

	/**
	 * 装载结果
	 * 
	 * @param results
	 */
	private void showData(SearchResult result) {
		JPanel dataView = ComponentRegedit.get("dataView");
		final JTextArea content = new JTextArea();
		content.setText(String.format(INITIAL_TEXT, result.getKey(), result.getValue(), result.getType(), result.getLocation()));
		content.setEditable(false);
		
		final JScrollPane scrollPane = new JScrollPane(content);
		scrollPane.setPreferredSize(new Dimension(dataView.getWidth() - 10, dataView.getHeight() - 10));

		dataView.add(scrollPane, BorderLayout.CENTER);
	}

	private static final class SearchResult {
		private String key;
		private String value;
		private String type;
		private String location;

		private SearchResult(String key, String value, String type, String location) {
			super();
			this.key = key;
			this.value = value;
			this.type = type;
			this.location = location;
		}

		public String getType() {
			return type;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public String getLocation() {
			return location;
		}

		@Override
		public String toString() {
			return "SearchResult [key=" + key + ", value=" + value + ", location=" + location + "]";
		}
	}
}
