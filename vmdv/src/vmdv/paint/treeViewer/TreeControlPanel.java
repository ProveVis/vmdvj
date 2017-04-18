package vmdv.paint.treeViewer;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;

public class TreeControlPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public TreeControlPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Tree overview", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTree tree = new JTree();
		panel.add(tree);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Layout", null, panel_1, null);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("New label");
		panel_1.add(lblNewLabel);
		
		JButton btnNewButton_1 = new JButton("New button");
		panel_1.add(btnNewButton_1);
		
		JButton btnNewButton = new JButton("New button");
		panel_1.add(btnNewButton);
		
		JComboBox comboBox = new JComboBox();
		panel_1.add(comboBox);

	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.getContentPane().add(new TreeControlPanel());
		frame.setSize(300, 600);
		frame.setLocation(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
