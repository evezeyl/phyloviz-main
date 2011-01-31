package net.phyloviz.tview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.TreeSet;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.ui.ChartLegendPanel;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.DataSet;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public class TViewPanel extends TopComponent {

	private final DataSet ds;
	private final CategoryProvider cp;
	private final ChartLegendPanel clp;
	private TablePanel table;
	private TreePanel tree;
	private JScrollPane sp;
	private final JTextField filterText;
	private JButton selectButton;
	private JButton resetButton;
	private JButton viewButton;
	private JRadioButton treeButton;
	private JRadioButton tableButton;
	private boolean tableortree;
	private boolean firstTime;
	private ButtonGroup group;

	/** Creates new form TViewPanel */
	public TViewPanel(String name, DataModel dm, DataSet _ds) {
		super(Lookups.singleton(dm));
		initComponents();
		this.setName(name);
		this.ds = _ds;

		cp = new CategoryProvider(dm);
		ds.add(cp);

		clp = new ChartLegendPanel(new Dimension(128, 128), cp, dm.weight());
		clp.setName(name + " (Selection view)");

		//the finders
		table = new TablePanel(dm);
		tree = new TreePanel(dm);
		firstTime = true;
		tableortree = true;
		sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBackground(Color.BLACK);
		filterText = new JTextField("");
		filterText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (tableortree) {
					table.releasedKey(filterText.getText());
				} else {
					tree.releasedKey(filterText.getText());
				}
			}
		});

		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Phyloviz.getWindow().removeTab("Chart");
				filterText.setText("");
				table.reseting();
				tree.reseting();
				cp.setSelection(null);
				clp.repaint();
			}
		});

		viewButton = new JButton("View");
		viewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				treeButton.setEnabled(false);
				tableButton.setEnabled(false);

				TreeSet<String>[] filter1;
				TreeSet<String>[] filter2;
				TreeSet<String>[] filter;
				filter1 = table.viewing();
				filter2 = tree.viewing();
				if (tableortree) {
					filter = filter1;
				} else {
					filter = filter2;
				}

				cp.setSelection(filter);
				clp.repaint();
				clp.open();
				clp.requestActive();

				treeButton.setEnabled(true);
				tableButton.setEnabled(true);
			}
		});

		selectButton = new JButton("Select");
		selectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				treeButton.setEnabled(false);
				tableButton.setEnabled(false);
				if (tableortree) {
					table.selecting(null, false);
				} else {
					tree.selecting(null, false);

				}
				treeButton.setEnabled(true);
				tableButton.setEnabled(true);
			}
		});

		treeButton = new JRadioButton("tree");
		tableButton = new JRadioButton("table");

		ItemListener radio1 = new RadioListener1();
		ItemListener radio2 = new RadioListener2();
		treeButton.addItemListener(radio1);
		tableButton.addItemListener(radio2);

		Box box = new Box(BoxLayout.X_AXIS);
		group = new ButtonGroup();
		box.add(new JLabel("View: "));
		box.add(tableButton);
		box.add(treeButton);
		group.add(tableButton);
		group.add(treeButton);
		tableButton.setSelected(true);
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(" Regex filter: "));
		box.add(Box.createHorizontalGlue());
		box.add(filterText);
		box.add(Box.createHorizontalGlue());
		box.add(selectButton);
		box.add(viewButton);
		box.add(resetButton);
		add(sp, BorderLayout.CENTER);
		add(box, BorderLayout.NORTH);
	}

	private class RadioListener1 implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			filterText.setText("");
			if (e.getStateChange() == ItemEvent.SELECTED) {
				tableortree = false;
				sp.setViewportView(tree);
				tree.selecting(table.getFilterSet(), true);
			}
		}
	}

	private class RadioListener2 implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				filterText.setText("");
				tableortree = true;
				sp.setViewportView(table);
				if (firstTime) {
					firstTime = false;
				} else {
					table.selecting(tree.getFilterSet(), true);
				}
			}
		}
	}

	@Override
	protected void componentOpened() {
		super.componentOpened();
		clp.open();
	}



	@Override
	protected void componentClosed() {
		super.componentClosed();
		ds.remove(cp);
		clp.close();
	}

	@Override
	public int getPersistenceType() {
		return PERSISTENCE_NEVER;
	}

	@Override
	protected String preferredID() {
		return "TViewer";
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                setLayout(new java.awt.BorderLayout());
        }// </editor-fold>//GEN-END:initComponents
        // Variables declaration - do not modify//GEN-BEGIN:variables
        // End of variables declaration//GEN-END:variables
}
