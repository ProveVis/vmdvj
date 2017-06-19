package vmdv.test;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class TestMenu extends JFrame {
	JMenuBar menubar = new JMenuBar();
	JMenu m1 = new JMenu("M1");
	
	public TestMenu() {
		this.setJMenuBar(menubar);
		menubar.add(new JMenu("menu1"));
		menubar.add(new JMenu("menu2"));
		menubar.add(m1);
		m1.add(new JMenuItem("menuitem"));
		
		this.setSize(300, 300);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		TestMenu tm = new TestMenu();
	}

}
