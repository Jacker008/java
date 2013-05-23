package single.yuxuanwang.jedisui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import single.yuxuanwang.jedisui.ComponentRegedit;
import single.yuxuanwang.jedisui.JedisPoolBeanFactory;

/**
 * 点击删除按钮
 * 
 * @author yuxuan.wang
 * 
 */
public class RemoveActionListener implements ActionListener {

	@Autowired
	private JedisPoolBeanFactory jedisPoolBeanFactory;

	public void actionPerformed(ActionEvent e) {
		JTextField searchKey = ComponentRegedit.get("searchKey");
		String key = searchKey.getText();
		ShardedJedisPool jedisPool = jedisPoolBeanFactory.getJedisPool();
		ShardedJedis resource = null;
		try {
			resource = jedisPool.getResource();
			if (resource.exists(key))
				resource.del(key);

			JButton findBt = ComponentRegedit.get("findBt");
			findBt.doClick();

			JButton regexBt = ComponentRegedit.get("regexBt");
			regexBt.doClick();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (resource != null)
				jedisPool.returnResource(resource);
		}
	}

}
