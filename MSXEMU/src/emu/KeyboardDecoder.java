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
			
			{'A', 2, 6}, {'B', 2, 7}, {'a', 2, 6}, {'b', 2, 7}, 

			{'C', 3, 0}, {'D', 3, 1}, {'E', 3, 2}, {'F', 3, 3}, {'G', 3, 4}, {'H', 3, 5}, 
			{'I', 3, 6}, {'J', 3, 7}, 
			{'c', 3, 0}, {'d', 3, 1}, {'e', 3, 2}, {'f', 3, 3}, {'g', 3, 4}, {'h', 3, 5}, 
			{'i', 3, 6}, {'j', 3, 7}, 

			{'K', 4, 0}, {'L', 4, 1}, {'M', 4, 2}, {'N', 4, 3}, {'O', 4, 4}, {'P', 4, 5}, 
			{'Q', 4, 6}, {'R', 4, 7}, 
			{'k', 4, 0}, {'l', 4, 1}, {'m', 4, 2}, {'n', 4, 3}, {'o', 4, 4}, {'p', 4, 5}, 
			{'q', 4, 6}, {'r', 4, 7}, 

			{'S', 5, 0}, {'T', 5, 1}, {'U', 5, 2}, {'V', 5, 3}, {'W', 5, 4}, {'X', 5, 5}, 
			{'Y', 5, 6}, {'Z', 5, 7}, 
			{'s', 5, 0}, {'t', 5, 1}, {'u', 5, 2}, {'v', 5, 3}, {'w', 5, 4}, {'x', 5, 5}, 
			{'y', 5, 6}, {'z', 5, 7} 

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
					System.out.println("Press key   row: " + charMatrix[i][1] + ", bit: " + charMatrix[i][2]);
				} else {
					depressKey(charMatrix[i][1], charMatrix[i][2]);
					System.out.println("Depress key row: " + charMatrix[i][1] + ", bit: " + charMatrix[i][2]);
				}
				success = true;
				break keySearchLoop;
			}
		}	
		
		/* Try to match key */
		if (!success) {
			int k = e.getKeyCode();
			for (int i = 0; i < keyMatrix.length; i++) {
				if (k == keyMatrix[i][0]) {
					if (state) {
						pressKey(keyMatrix[i][1], keyMatrix[i][2]);
						System.out.println("Press key   row: " + keyMatrix[i][1] + ", bit: " + keyMatrix[i][2]);
					} else {
						depressKey(keyMatrix[i][1], keyMatrix[i][2]);
						System.out.println("Depress key row: " + keyMatrix[i][1] + ", bit: " + keyMatrix[i][2]);
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
