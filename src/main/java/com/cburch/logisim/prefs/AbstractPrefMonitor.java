/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.prefs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

abstract class AbstractPrefMonitor<E> implements PrefMonitor<E> {
  private final String name;

  AbstractPrefMonitor(String name) {
    this.name = name;
  }

  @Override
public void addPropertyChangeListener(PropertyChangeListener listener) {
    AppPreferences.addPropertyChangeListener(name, listener);
  }

  @Override
public boolean getBoolean() {
    return (Boolean) get();
  }

  @Override
public String getIdentifier() {
    return name;
  }

  @Override
public boolean isSource(PropertyChangeEvent event) {
    return name.equals(event.getPropertyName());
  }

  @Override
public void removePropertyChangeListener(PropertyChangeListener listener) {
    AppPreferences.removePropertyChangeListener(name, listener);
  }

  @Override
public void setBoolean(boolean value) {
    @SuppressWarnings("unchecked")
    E valObj = (E) Boolean.valueOf(value);
    set(valObj);
  }
}
