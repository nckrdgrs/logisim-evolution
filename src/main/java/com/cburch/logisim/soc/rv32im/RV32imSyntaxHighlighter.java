/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.soc.rv32im;

import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

import com.cburch.logisim.soc.data.AssemblerHighlighter;

public class RV32imSyntaxHighlighter extends AssemblerHighlighter {
  @Override
  public TokenMap getWordsToHighlight() {
    TokenMap map = super.getWordsToHighlight();
    for (String registerABIName : RV32imState.registerABINames)
		map.put(registerABIName, TokenTypes.OPERATOR);
    map.put("pc", TokenTypes.OPERATOR);
    for (int i = 0; i < 32; i++) map.put("x" + i, TokenTypes.OPERATOR);
    for (String opcode : RV32imState.ASSEMBLER.getOpcodes())
      map.put(opcode.toLowerCase(), TokenTypes.RESERVED_WORD);
    return map;
  }
}
