package single.yuxuanwang.jedisui.listener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import single.yuxuanwang.jedisui.ComponentRegedit;
import single.yuxuanwang.jedisui.JedisPoolBeanFactory;

/**
 * 正则查询key
 * 
 * @author wangyuxuan
 * 
 */
public class RegexButtonListener implements ActionListener {

	@Autowired
	private JedisPoolBeanFactory jedisPoolBeanFactory;

	public void actionPerformed(ActionEvent e) {
		JTextField searchKey = ComponentRegedit.get("regexSearchKey");
		String key = searchKey.getText();
		if (StringUtils.isNotBlank(key)) {
			ShardedJedisPool jedisPool = jedisPoolBeanFactory.getJedisPool();
			ShardedJedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				Map<String, Set<String>> keyMap = searchData(key, jedis);
				showData(keyMap);
			} finally {
				if (jedis != null)
					jedisPool.returnResource(jedis);
			}
		}
	}

	/**
	 * 搜索key
	 * 
	 * @param key
	 * @param jedis
	 * @return
	 */
	private Map<String, Set<String>> searchData(String key, ShardedJedis jedis) {
		Map<String, Set<String>> keyMap = new LinkedHashMap<String, Set<String>>();
		Collection<Jedis> shards = jedis.getAllShards();
		if (shards != null) {
			for (Jedis shard : shards) {
				Set<String> matchedKeys = shard.keys(key);
				if (matchedKeys != null)
					keyMap.put(shard.getClient().getHost(), matchedKeys);
			}
		}
		return keyMap;
	}

	private static final String[] TABLE_TITLE = new String[] { "KEY", "LOCATION" };

	/**
	 * 填充数据到显示面板
	 * 
	 * @param keyMap
	 */
	private void showData(Map<String, Set<String>> keyMap) {
		JPanel dataView = ComponentRegedit.get("regexDataView");
		dataView.removeAll();
		if (!keyMap.isEmpty()) {
			String[][] rowData = new String[items(keyMap)][2];
			int index = 0;
			for (Entry<String, Set<String>> entry : keyMap.entrySet()) {
				String entryKey = entry.getKey();
				for (String key : entry.getValue()) {
					rowData[index] = new String[] { key, entryKey };
					index++;
				}
			}
			final JTable table = new JTable(rowData, TABLE_TITLE);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			table.getColumnModel().getColumn(0).setPreferredWidth(50);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			ListSelectionModel row = table.getSelectionModel();
			row.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return;
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (!lsm.isSelectionEmpty()) {
						JTextField inputView = ComponentRegedit.get("searchKey");
						inputView.setText(table.getValueAt(lsm.getMinSelectionIndex(), 0).toString());
						
						JButton findBt = ComponentRegedit.get("findBt");
						findBt.doClick();
					}
				}
			});

			final JScrollPane scrollPane = new JScrollPane(table);

			scrollPane.setPreferredSize(new Dimension(dataView.getWidth() - 10, dataView.getHeight() - 10));

			dataView.add(scrollPane, BorderLayout.CENTER);
		}

		dataView.validate();
		dataView.repaint();
	}

	private int items(Map<String, Set<String>> keyMap) {
		int items = 0;
		for (Set<String> value : keyMap.values()) {
			items += value.size();
		}
		return items;
	}

}
