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

import vmdv.paint.treeViewer.SearchByIdAffect;
import vmdv.paint.treeViewer.TreeVisualizer;

public class SearchByIdDlg extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JCheckBox checkBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SearchByIdDlg dialog = new SearchByIdDlg(null, false);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SearchByIdDlg(final TreeVisualizer tv, boolean modal) {
		super(tv.mainFrame, modal);
		setTitle("Search By ID");
		setBounds(100, 100, 450, 244);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblId = new JLabel("ID:");
		lblId.setBounds(50, 63, 65, 28);
		contentPanel.add(lblId);
		
		textField = new JTextField();
		textField.setBounds(129, 65, 180, 24);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		checkBox = new JCheckBox("Highlight Subtree");
		checkBox.setBounds(129, 113, 180, 27);
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
//						SearchByIdDlg.this.showText("");
						SearchByIdDlg.this.setVisible(false);
						tv.listener.addAffect(new SearchByIdAffect(tv, tv.tree, getText(), isHighlightSubtree()));
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
//						ShowTextDlg.this.showText("");
						SearchByIdDlg.this.setVisible(false);
					}
					
				});
			}
		}
	}
	
	public String getText() {
		return textField.getText().trim();
	}
	
	public boolean isHighlightSubtree() {
		return checkBox.isSelected();
	}
}
