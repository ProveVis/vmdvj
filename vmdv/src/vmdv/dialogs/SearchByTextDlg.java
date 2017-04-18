package vmdv.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import vmdv.paint.treeViewer.SearchByTextAffect;
import vmdv.paint.treeViewer.TreeVisualizer;

public class SearchByTextDlg extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	JCheckBox checkBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SearchByTextDlg dialog = new SearchByTextDlg(null, false);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SearchByTextDlg(final TreeVisualizer tv, boolean modal) {
		super(tv.mainFrame, modal);
		setTitle("Search By Text");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Text");
			lblNewLabel.setBounds(62, 79, 52, 18);
			contentPanel.add(lblNewLabel);
		}
		
		textField = new JTextField();
		textField.setBounds(117, 76, 266, 24);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		checkBox = new JCheckBox("Hightlight Subtree");
		checkBox.setBounds(117, 134, 195, 27);
		contentPanel.add(checkBox);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						
						SearchByTextDlg.this.setVisible(false);
						tv.listener.addAffect(new SearchByTextAffect(tv, tv.tree, getText(), isHightlightSubtree()));
						textField.setText("");
						checkBox.setSelected(false);
					}
					
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						textField.setText("");
						checkBox.setSelected(false);
						SearchByTextDlg.this.setVisible(false);
					}
					
				});
			}
		}
	}
	
	public String getText() {
		return textField.getText().trim();
	}
	
	public boolean isHightlightSubtree() {
		return checkBox.isSelected();
	}
}
