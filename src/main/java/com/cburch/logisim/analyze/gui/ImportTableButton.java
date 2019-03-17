/*******************************************************************************
 * This file is part of logisim-evolution.
 *
 *   logisim-evolution is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   logisim-evolution is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Original code by Carl Burch (http://www.cburch.com), 2011.
 *   Subsequent modifications by :
 *     + Haute École Spécialisée Bernoise
 *       http://www.bfh.ch
 *     + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *       http://hepia.hesge.ch/
 *     + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *       http://www.heig-vd.ch/
 *   The project is currently maintained by :
 *     + REDS Institute - HEIG-VD
 *       Yverdon-les-Bains, Switzerland
 *       http://reds.heig-vd.ch
 *******************************************************************************/

package com.cburch.logisim.analyze.gui;

import static com.cburch.logisim.analyze.Strings.S;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.cburch.logisim.analyze.file.TruthtableCsvFile;
import com.cburch.logisim.analyze.file.TruthtableTextFile;
import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.util.JFileChoosers;

public class ImportTableButton extends JButton {

	private static final long serialVersionUID = 1L;

	private JFrame parent;
	private AnalyzerModel model;

	ImportTableButton(JFrame parent, AnalyzerModel model) {
		this.parent = parent;
		this.model = model;
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				doLoad();
			}
		});
	}

	void localeChanged() {
		setText(S.get("importTableButton"));
	}

	private File lastFile = null;
	void doLoad() {
		if (lastFile == null) {
			Circuit c = model.getCurrentCircuit();
			if (c != null)
				lastFile = new File(c.getName() + ".txt");
			else
				lastFile = new File("truthtable.txt");
		}
		JFileChooser chooser = JFileChoosers.createSelected(lastFile);
		chooser.setDialogTitle(S.get("openButton"));
		chooser.addChoosableFileFilter(TruthtableTextFile.FILE_FILTER);
		chooser.addChoosableFileFilter(TruthtableCsvFile.FILE_FILTER);
		chooser.setFileFilter(TruthtableTextFile.FILE_FILTER);
		int choice = chooser.showOpenDialog(parent);
		if (choice == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file.isDirectory()) {
				JOptionPane.showMessageDialog(parent,
						S.fmt("notFileMessage", file.getName()),
						S.get("openErrorTitle"), JOptionPane.OK_OPTION);
				return;
			}
			if (!file.exists() || !file.canRead()) {
				JOptionPane.showMessageDialog(parent,
						S.fmt("cantReadMessage", file.getName()),
						S.get("openErrorTitle"), JOptionPane.OK_OPTION);
				return;
			}
			try {
				String FileName = file.getName();
				int idx = FileName.lastIndexOf(".");
				String ext = FileName.substring(idx+1);
				if (ext.equals("txt"))
					TruthtableTextFile.doLoad(file,model,parent);
				else if (ext.equals("csv"))
					TruthtableCsvFile.doLoad(file,model,parent);
				else {
					JOptionPane.showMessageDialog(parent,
							S.fmt("DoNotKnowHowto",FileName),
							S.get("openErrorTitle"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				lastFile = file;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(parent,
						e.getMessage(),
						S.get("openErrorTitle"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}