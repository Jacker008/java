package single.yuxuanwang.jedisui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wangyuxuan
 * 
 */
public class FrameInitail {
	
	@Autowired
	private MainFrame mainFrame;
	
	@Autowired
	private SearchFrame searchFrame;
	
	public void showMainFrame() {
		JFrame frame = new JFrame("Jedis Client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1100, 600);
		
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		splitPane.setLeftComponent(mainFrame);
		splitPane.setRightComponent(searchFrame);
		
		splitPane.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				splitPane.setDividerLocation(0.5);
			}
		});
		
		splitPane.setDividerSize(5);
		
		frame.add(splitPane);

		// frame.pack();
		frame.setVisible(true);
	}

}
