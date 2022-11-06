/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.soc.data;

import java.util.HashSet;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

public class AssemblerHighlighter extends AbstractTokenMaker {
  public static final int REPEAT_LAST = -1;
  public static final int DOUBLE_QUOTE_END = -2;
  public static final int MAYBE_SHIFT_LEFT = -3;
  public static final int MAYBE_SHIFT_RIGHT = -4;
  public static final int SHIFT_END = -5;
  private boolean escape = false;

  private static final String[] directives = {
      ".ascii", ".align", ".file", ".globl", ".local", ".comm", ".common", ".ident",
      ".section", ".size", ".text", ".data", ".rodata", ".bss", ".string", ".p2align",
      ".asciz", ".equ", ".macro", ".endm", ".type", ".option", ".byte", ".2byte", ".half",
      ".short", ".4byte", ".word", ".long", ".8byte", ".dword", ".quad", ".balign",
      ".zero", ".org"};

  @SuppressWarnings("serial")
  public static final HashSet<String> BYTES = new HashSet<>() {{
      add(".byte");
    }};
  @SuppressWarnings("serial")
  public static final HashSet<String> SHORTS = new HashSet<>() {{
      add(".half");
      add(".2byte");
      add(".short");
    }};
  @SuppressWarnings("serial")
  public static final HashSet<String> INTS = new HashSet<>() {{
      add(".word");
      add(".4byte");
      add(".long");
    }};
  @SuppressWarnings("serial")
  public static final HashSet<String> LONGS = new HashSet<>() {{
      add(".dword");
      add(".8byte");
      add(".quad");
    }};
  @SuppressWarnings("serial")
  public static final HashSet<String> STRINGS = new HashSet<>() {{
      add(".ascii");
      add(".asciz");
      add(".string");
    }};


  @Override
  public TokenMap getWordsToHighlight() {
    TokenMap map = new TokenMap();
    for (String directive : directives)
      map.put(directive, TokenTypes.FUNCTION);
    return map;
  }

  @Override
  public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
    // This assumes all keywords, etc. were parsed as "identifiers."
    if (tokenType == TokenTypes.IDENTIFIER) {
      int value = wordsToHighlight.get(segment, start, end);
      if (value != -1) {
        tokenType = value;
      }
    }
    super.addToken(segment, start, end, tokenType, startOffset);
  }

  private int check(Segment text, char kar, int currentToken, int start, int index, int newStart) {
    int currentTokenType = currentToken >= 0 ? currentToken : TokenTypes.LITERAL_CHAR;
    if (currentTokenType == TokenTypes.COMMENT_EOL) return TokenTypes.COMMENT_EOL;
    if (currentTokenType == TokenTypes.LITERAL_STRING_DOUBLE_QUOTE && (kar != '"' || escape)) {
      escape = kar == '\\';
      return TokenTypes.LITERAL_STRING_DOUBLE_QUOTE;
    }
    switch (kar) {
      case ' ':
      case '\t':
        if (currentTokenType != TokenTypes.NULL && currentTokenType != TokenTypes.WHITESPACE)
          addToken(text, start, index - 1, currentTokenType, newStart);
        return TokenTypes.WHITESPACE;
      case '"':
        if (currentTokenType == TokenTypes.LITERAL_STRING_DOUBLE_QUOTE) {
          addToken(text, start, index, currentTokenType, newStart);
          return DOUBLE_QUOTE_END;
        }
        if (currentTokenType != TokenTypes.NULL)
          addToken(text, start, index - 1, currentTokenType, newStart);
        escape = false;
        return TokenTypes.LITERAL_STRING_DOUBLE_QUOTE;
      case '#':
        if (currentTokenType != TokenTypes.NULL)
          addToken(text, start, index - 1, currentTokenType, newStart);
        return TokenTypes.COMMENT_EOL;
      case '<':
        if (currentToken != MAYBE_SHIFT_LEFT) {
          if (currentTokenType != TokenTypes.NULL)
            addToken(text, start, index - 1, currentTokenType, newStart);
          return MAYBE_SHIFT_LEFT;
        } else {
          addToken(text, start, index, currentTokenType, newStart);
          return SHIFT_END;
        }
      case '>':
        if (currentToken != MAYBE_SHIFT_RIGHT) {
          if (currentTokenType != TokenTypes.NULL)
            addToken(text, start, index - 1, currentTokenType, newStart);
          return MAYBE_SHIFT_RIGHT;
        } else {
          addToken(text, start, index, currentTokenType, newStart);
          return SHIFT_END;
        }
      case '@':
        if (currentTokenType != TokenTypes.NULL)
          addToken(text, start, index - 1, currentTokenType, newStart);
        return currentTokenType == TokenTypes.PREPROCESSOR ? REPEAT_LAST : TokenTypes.PREPROCESSOR;
      case '(':
      case ')':
      case '{':
      case '}':
      case '[':
      case ',':
      case ':':
      case '+':
      case '-':
      case '*':
      case '/':
      case '%':
      case ']':
        if (currentTokenType != TokenTypes.NULL)
          addToken(text, start, index - 1, currentTokenType, newStart);
        return currentTokenType == TokenTypes.LITERAL_CHAR ? REPEAT_LAST : TokenTypes.LITERAL_CHAR;
      case 'x':
      case 'X':
        if (currentTokenType == TokenTypes.LITERAL_NUMBER_DECIMAL_INT) {
          return TokenTypes.LITERAL_NUMBER_HEXADECIMAL;
        }
    }
    if (currentTokenType == TokenTypes.IDENTIFIER) return TokenTypes.IDENTIFIER;
    if (RSyntaxUtilities.isDigit(kar)) {
      if (currentTokenType == TokenTypes.PREPROCESSOR) return TokenTypes.PREPROCESSOR;
      if (currentTokenType != TokenTypes.NULL
          && currentTokenType != TokenTypes.LITERAL_NUMBER_DECIMAL_INT
          && currentTokenType != TokenTypes.LITERAL_NUMBER_HEXADECIMAL)
        addToken(text, start, index - 1, currentTokenType, newStart);
      return currentTokenType != TokenTypes.LITERAL_NUMBER_HEXADECIMAL
          ? TokenTypes.LITERAL_NUMBER_DECIMAL_INT
          : currentTokenType;
    }
    if (RSyntaxUtilities.isHexCharacter(kar)
        && currentTokenType == TokenTypes.LITERAL_NUMBER_HEXADECIMAL) return currentTokenType;
    if (currentTokenType != TokenTypes.NULL)
      addToken(text, start, index - 1, currentTokenType, newStart);
    return TokenTypes.IDENTIFIER;
  }

  @Override
  public Token getTokenList(Segment arg0, int arg1, int arg2) {
    resetTokenList();

    char[] array = arg0.array;
    int offset = arg0.offset;
    int count = arg0.count;
    int end = offset + count;
    int newStartOffset = arg2 - offset;

    int currentTokenStart = offset;
    int currentTokenType = arg1;

    escape = false;
    for (int i = offset; i < end; i++) {
      char c = array[i];
      int newTokenType =
          check(
              arg0, c, currentTokenType, currentTokenStart, i, newStartOffset + currentTokenStart);
      if (newTokenType != currentTokenType
          && !(newTokenType == TokenTypes.LITERAL_NUMBER_HEXADECIMAL
              && currentTokenType == TokenTypes.LITERAL_NUMBER_DECIMAL_INT)) currentTokenStart = i;
      if (newTokenType == DOUBLE_QUOTE_END || newTokenType == SHIFT_END) {
        currentTokenStart = i + 1;
        currentTokenType = TokenTypes.NULL;
      } else if (newTokenType == REPEAT_LAST) currentTokenStart = i;
      else currentTokenType = newTokenType;
    }
    switch (currentTokenType) {
      case TokenTypes.LITERAL_STRING_DOUBLE_QUOTE -> addToken(
          arg0, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
      case TokenTypes.NULL -> addNullToken();
      default -> {
        addToken(
            arg0, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
        addNullToken();
      }
    }
    return firstToken;
  }
}
