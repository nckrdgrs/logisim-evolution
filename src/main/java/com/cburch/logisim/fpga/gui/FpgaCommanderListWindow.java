/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.fpga.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;

import com.cburch.contracts.BaseListDataListenerContract;
import com.cburch.contracts.BaseWindowListenerContract;
import com.cburch.logisim.fpga.data.FpgaCommanderListModel;

@SuppressWarnings("serial")
public class FpgaCommanderListWindow extends JFrame
    implements BaseWindowListenerContract, BaseListDataListenerContract {

  private final String Title;
  private final JList<Object> textArea = new JList<>();
  private boolean IsActive = false;
  private final boolean count;
  private final FpgaCommanderListModel model;
  private final JScrollPane textMessages;

  public FpgaCommanderListWindow(
      String Title, Color fg, boolean count, FpgaCommanderListModel model) {
    super((count) ? Title + " (" + model.getCountNr() + ")" : Title);
    this.Title = Title;
    setResizable(true);
    setAlwaysOnTop(false);
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    Color bg = Color.black;

    textArea.setBackground(bg);
    textArea.setForeground(fg);
    textArea.setSelectionBackground(fg);
    textArea.setSelectionForeground(bg);
    textArea.setFont(new Font("monospaced", Font.PLAIN, 14));
    textArea.setModel(model);
    textArea.setCellRenderer(model.getMyRenderer());
    textArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    model.addListDataListener(this);

    textMessages = new JScrollPane(textArea);
    textMessages.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    textMessages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(textMessages);
    setLocationRelativeTo(null);
    pack();
    addWindowListener(this);
    this.count = count;
    this.model = model;
  }

  public boolean isActivated() {
    return IsActive;
  }

  public JList<Object> getListObject() {
    return textArea;
  }

  @Override
  public void windowClosing(WindowEvent e) {
    IsActive = false;
    setVisible(false);
  }

  @Override
  public void windowActivated(WindowEvent e) {
    IsActive = true;
  }

  @Override
  public void contentsChanged(ListDataEvent e) {
    setTitle((count) ? Title + " (" + model.getCountNr() + ")" : Title);
    this.revalidate();
    this.repaint();
  }
}
