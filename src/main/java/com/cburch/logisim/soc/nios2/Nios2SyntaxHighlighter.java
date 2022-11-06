/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.soc.nios2;

import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

import com.cburch.logisim.soc.data.AssemblerHighlighter;

// FIXME: this class seems to be unused
public class Nios2SyntaxHighlighter extends AssemblerHighlighter {
  @Override
  public TokenMap getWordsToHighlight() {
    TokenMap map = super.getWordsToHighlight();
    for (String registerABIName : Nios2State.registerABINames)
		map.put(registerABIName, TokenTypes.OPERATOR);
    map.put("pc", TokenTypes.OPERATOR);
    for (int i = 0; i < 32; i++) {
      map.put("r" + i, TokenTypes.OPERATOR);
      map.put("c" + i, TokenTypes.OPERATOR);
      map.put("ctl" + i, TokenTypes.OPERATOR);
    }
    for (String opcode : Nios2State.ASSEMBLER.getOpcodes())
      map.put(opcode.toLowerCase(), TokenTypes.RESERVED_WORD);
    return map;
  }
}
