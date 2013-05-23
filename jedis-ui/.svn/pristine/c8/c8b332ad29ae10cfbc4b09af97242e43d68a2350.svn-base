package single.yuxuanwang.jedisui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

/**
 * 组件注册表
 * 
 * @author wangyuxuan
 * 
 */
public class ComponentRegedit {

	private static final Map<String, JComponent> REGESTERS = new HashMap<String, JComponent>();

	public static final void register(String id, JComponent component) {
		REGESTERS.put(id, component);
	}

	@SuppressWarnings("unchecked")
	public static final <T extends JComponent> T get(String id) {
		return (T) REGESTERS.get(id);
	}

}
