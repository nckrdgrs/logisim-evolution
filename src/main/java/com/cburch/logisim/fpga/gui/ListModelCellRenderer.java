package com.cburch.logisim.fpga.gui;

import static com.cburch.logisim.fpga.Strings.S;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.cburch.logisim.fpga.designrulecheck.SimpleDRCContainer;
import com.cburch.logisim.util.Icons;

public class ListModelCellRenderer extends JLabel implements ListCellRenderer<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private boolean CountLines;
	
	private static Color FATAL = Color.RED;
	private static Color SEVERE = Color.yellow;
	private static Color NORMAL = Color.LIGHT_GRAY;
	private static Color ADDENDUM = Color.GRAY;

	public ListModelCellRenderer(boolean countLines) {
		CountLines = countLines;
		setOpaque(true);
	}
	
	@Override
	public Component getListCellRendererComponent(
			JList list, 
			Object value, 
			int index,
			boolean isSelected, 
			boolean cellHasFocus) {
		SimpleDRCContainer msg = null;
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		StringBuffer Line = new StringBuffer();
		setIcon(Icons.getIcon("empty.png")); /* place holder too make space for the trace icon */
		if (value instanceof SimpleDRCContainer) {
			msg = (SimpleDRCContainer) value;
		}
		if (msg != null) {
			if (msg.DRCInfoPresent()) {
	        	setIcon(Icons.getIcon("drc_trace.png"));
			} 
			switch (msg.Severity()) {
				case SimpleDRCContainer.LEVEL_SEVERE :
					setForeground(SEVERE);
					break;
				case SimpleDRCContainer.LEVEL_FATAL :
					setBackground(FATAL);
					setForeground(list.getBackground());
					break;
				default : 
					setForeground(NORMAL);
			}
		}
		if (value.toString().contains("BUG")) {
			setBackground(Color.MAGENTA);
			setForeground(Color.black);
		}
		if (CountLines) {
			if (msg != null) {
				if (msg.SupressCount()) {
					setForeground(ADDENDUM);
					Line.append("       ");
				} else {
					int line = msg.GetListNumber();
					if (line < 10) {
						Line.append("    ");
					} else if (line < 100) {
						Line.append("   ");
					} else if (line < 1000) {
						Line.append("  ");
					} else if (line < 10000) {
						Line.append(" ");
					}
					Line.append(Integer.toString(line) + "> ");
				}
			} else {
				if (index < 9) {
					Line.append("    ");
				} else if (index < 99) {
					Line.append("   ");
				} else if (index < 999) {
					Line.append("  ");
				} else if (index < 9999) {
					Line.append(" ");
				}
				Line.append(Integer.toString(index + 1) + "> ");
			}
		}
		if (msg != null) {
			switch (msg.Severity()) {
				case SimpleDRCContainer.LEVEL_SEVERE :
					Line.append(S.get("SEVERE_MSG")+" ");
					break;
				case SimpleDRCContainer.LEVEL_FATAL :
					Line.append(S.get("FATAL_MSG")+" ");
					break;
			}
			if (msg.HasCircuit()) {
				Line.append(msg.GetCircuit().getName()+": ");
			}
		}
		Line.append(value.toString());
		setText(Line.toString());
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		return this;
	}

}