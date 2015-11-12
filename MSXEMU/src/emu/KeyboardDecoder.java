package emu;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardDecoder implements KeyListener {

	private byte[] rows = new byte[11];
	
	private int[][] charMatrix = {
			{'0', 0, 0}, {')', 0, 0}, {'1', 0, 1}, {'!', 0, 1}, {'2', 0, 2}, {'@', 0, 2},
			{'3', 0, 3}, {'#', 0, 3}, {'4', 0, 4}, {'$', 0, 4}, {'5', 0, 5}, {'%', 0, 5},
			{'6', 0, 6}, {'&', 0, 6}, {'7', 0, 7}, {'\'', 0, 7}, 
			
			{'8', 1, 0}, {'*', 1, 0}, {'9', 1, 1}, {'(', 1, 1}, {'-', 1, 2}, {'_', 1, 2}, 
			{'=', 1, 3}, {'+', 1, 3}, {'\\', 1, 4}, {'|', 1, 4}, {'[', 1, 5}, {'{', 1, 5}, 
			
			{'A', 1, 6}, {'B', 1, 7}, {'a', 1, 6}, {'b', 1, 7}, 

			{'C', 2, 0}, {'D', 2, 1}, {'E', 2, 2}, {'F', 2, 3}, {'G', 2, 4}, {'H', 2, 5}, 
			{'I', 2, 6}, {'J', 2, 7}, 
			{'C', 2, 0}, {'D', 2, 1}, {'E', 2, 2}, {'F', 2, 3}, {'G', 2, 4}, {'H', 2, 5}, 
			{'I', 2, 6}, {'J', 2, 7}, 

			{'K', 3, 0}, {'L', 3, 1}, {'M', 3, 2}, {'N', 3, 3}, {'O', 3, 4}, {'P', 3, 5}, 
			{'Q', 3, 6}, {'R', 3, 7}, 
			{'k', 3, 0}, {'l', 3, 1}, {'m', 3, 2}, {'n', 3, 3}, {'o', 3, 4}, {'p', 3, 5}, 
			{'q', 3, 6}, {'r', 3, 7}, 

			{'S', 4, 0}, {'T', 4, 1}, {'U', 4, 2}, {'V', 4, 3}, {'W', 4, 4}, {'X', 4, 5}, 
			{'Y', 4, 6}, {'Z', 4, 7}, 
			{'s', 4, 0}, {'t', 4, 1}, {'u', 4, 2}, {'v', 4, 3}, {'w', 4, 4}, {'x', 4, 5}, 
			{'y', 4, 6}, {'z', 4, 7} 

	};

	private int[][] keyMatrix = {
			{KeyEvent.VK_SHIFT, 		6, 0},
			{KeyEvent.VK_CONTROL,		6, 1},
			{KeyEvent.VK_ALT_GRAPH, 	6, 2}, // GRAPH ??
			{KeyEvent.VK_CAPS_LOCK,		6, 3},
			{KeyEvent.VK_CODE_INPUT, 	6, 4}, // CODE ??
			{KeyEvent.VK_F1, 			6, 5},
			{KeyEvent.VK_F2, 			6, 6},
			{KeyEvent.VK_F3, 			6, 7},
			
			{KeyEvent.VK_F4, 			7, 0},
			{KeyEvent.VK_F5,			7, 1},
			{KeyEvent.VK_ESCAPE, 		7, 2}, 
			{KeyEvent.VK_TAB,			7, 3},
			{KeyEvent.VK_STOP, 			7, 4}, // STOP ??
			{KeyEvent.VK_BACK_SPACE,	7, 5},
			{KeyEvent.VK_F6, 			7, 6}, // SELECT ??
			{KeyEvent.VK_ENTER, 		7, 7},
			
			{KeyEvent.VK_SPACE,			8, 0},
			{KeyEvent.VK_HOME,			8, 1},
			{KeyEvent.VK_INSERT, 		8, 2}, 
			{KeyEvent.VK_DELETE,		8, 3},
			{KeyEvent.VK_LEFT, 			8, 4}, 
			{KeyEvent.VK_UP,			8, 5},
			{KeyEvent.VK_DOWN, 			8, 6}, 
			{KeyEvent.VK_RIGHT, 		8, 7},
	
			/* We ignore row 9 and 10 (numeric pad keys) */
	};
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		processKeyEvent(e, true);
	}

	
	private void processKeyEvent(KeyEvent e, boolean state) {
		/* Try to match character */
		int c = (int)e.getKeyChar();
		boolean success = false;
		keySearchLoop:
		for (int i = 0; i < charMatrix.length; i++) {
			if (c == charMatrix[i][0]) {
				if (state) {
					pressKey(charMatrix[i][1], charMatrix[i][2]);
				} else {
					depressKey(charMatrix[i][1], charMatrix[i][2]);
				}
				success = true;
				break keySearchLoop;
			}
		}	
		
		/* Try to match key */
		if (!success) {
			int k = e.getKeyCode();
			for (int i = 0; i < charMatrix.length; i++) {
				if (k == keyMatrix[i][0]) {
					if (state) {
						pressKey(keyMatrix[i][1], keyMatrix[i][2]);
					} else {
						depressKey(keyMatrix[i][1], keyMatrix[i][2]);
					}
				}
			}
		}
	}

	private void pressKey(int row, int bit) {
		rows[row] = (byte)(rows[row] | (1 << bit));
	}

	private void depressKey(int row, int bit) {
		rows[row] = (byte)(rows[row] & ~(1 << bit));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		processKeyEvent(e, false);
	}
	
	public byte getRowValue(int row) {
		return rows[row];
	}

	public void setCapslock(boolean state) {
		if (state != Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
			Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, state);
		}
	}

}
