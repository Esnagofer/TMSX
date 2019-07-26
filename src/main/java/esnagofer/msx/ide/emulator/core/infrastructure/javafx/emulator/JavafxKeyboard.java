/*
 * 
 */
package esnagofer.msx.ide.emulator.core.infrastructure.javafx.emulator;

import java.util.Optional;

import esnagofer.msx.ide.emulator.core.domain.model.emulator.components.keyboard.Keyboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


/**
 * The Class KeyboardDecoder.
 */
public class JavafxKeyboard implements Keyboard {

	/** The rows. */
	/*
	 * This array contains the MSX key matrix (11 rows of 8 bits each)
	 * The array is updated by the pressKey and depressKey methods and
	 * is read out by the getRowValue method.
	 */
	private byte[] rows = new byte[11];
	
	/** The char matrix. */
	/* 
	 * Matrix for translating characters to key matrix codes. 
	 * Every entry is of the form {character, row, column},
	 * meaning that character is mapped to the matrix at position
	 * row/column.
	 */
	private int[][] charMatrix = {
			{'0', 0, 0}, {')', 0, 0}, {'1', 0, 1}, {'!', 0, 1}, {'2', 0, 2}, {'@', 0, 2},
			{'3', 0, 3}, {'#', 0, 3}, {'4', 0, 4}, {'$', 0, 4}, {'5', 0, 5}, {'%', 0, 5},
			{'6', 0, 6}, {'&', 0, 6}, {'7', 0, 7}, {'\'', 0, 7}, 
			
			{'8', 1, 0}, {'*', 1, 0}, {'9', 1, 1}, {'(', 1, 1}, {'-', 1, 2}, {'_', 1, 2}, 
			{'=', 1, 3}, {'+', 1, 3}, {'\\', 1, 4}, {'|', 1, 4}, {'[', 1, 5}, {'{', 1, 5}, 
			{']', 1, 6}, {'}', 1, 6}, {';', 1, 7}, {':', 1, 7}, 
			
			{'A', 2, 6}, {'B', 2, 7}, {'a', 2, 6}, {'b', 2, 7},
			{'/', 2, 4}, {'?', 2, 4}, {'.', 2, 3}, {'>', 2, 3},
			{',', 2, 2}, {'<', 2, 2}, {'`', 2, 1}, {'~', 2, 1},
			{'\'', 2, 0}, {'"', 2, 0},
			
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

	/** The key matrix. */
	/* 
	 * Matrix for translating special keys to key matrix codes. 
	 * Every entry is of the form {KeyEvent, row, column},
	 * meaning that KeyEvent is mapped to the matrix at position
	 * row/column.
	 */

	
	private Optional<JavafxScreenRowBit> rowBitFromKeyCode(KeyCode keyCode) {
		JavafxScreenRowBit rowBit = null;
		
		switch (keyCode) {
		
		// 6.0 to 6.7
		case SHIFT: rowBit = 		JavafxScreenRowBit.valueOf(6,0); break;
		case CONTROL: rowBit = 		JavafxScreenRowBit.valueOf(6,1); break;
		case ALT_GRAPH: rowBit = 	JavafxScreenRowBit.valueOf(6,2); break;
		case CAPS: rowBit = 		JavafxScreenRowBit.valueOf(6,3); break;
		case CODE_INPUT: rowBit = 	JavafxScreenRowBit.valueOf(6,4); break;
		case F1: rowBit = 			JavafxScreenRowBit.valueOf(6,5); break;
		case F2: rowBit = 			JavafxScreenRowBit.valueOf(6,6); break;
		case F3: rowBit = 			JavafxScreenRowBit.valueOf(6,7); break;

		// 7.0 to 7.7
		case F4: rowBit = 			JavafxScreenRowBit.valueOf(7,0); break;
		case F5: rowBit = 			JavafxScreenRowBit.valueOf(7,1); break;
		case ESCAPE: rowBit = 		JavafxScreenRowBit.valueOf(7,2); break;
		case TAB: rowBit = 			JavafxScreenRowBit.valueOf(7,3); break;
		case STOP: rowBit = 		JavafxScreenRowBit.valueOf(7,4); break;
		case BACK_SPACE: rowBit = 	JavafxScreenRowBit.valueOf(7,5); break;
		case F6: rowBit = 			JavafxScreenRowBit.valueOf(7,6); break;
		case ENTER: rowBit = 		JavafxScreenRowBit.valueOf(7,7); break;

		// 8.0 to 8.7
		case SPACE: rowBit = 		JavafxScreenRowBit.valueOf(8,0); break;
		case HOME: rowBit = 		JavafxScreenRowBit.valueOf(8,1); break;
		case INSERT: rowBit = 		JavafxScreenRowBit.valueOf(8,2); break;
		case DELETE: rowBit = 		JavafxScreenRowBit.valueOf(8,3); break;
		case LEFT: rowBit = 		JavafxScreenRowBit.valueOf(8,4); break;
		case UP: rowBit = 			JavafxScreenRowBit.valueOf(8,5); break;
		case DOWN: rowBit = 		JavafxScreenRowBit.valueOf(8,6); break;
		case RIGHT: rowBit = 		JavafxScreenRowBit.valueOf(8,7); break;
		
		/* We ignore row 9 and 10 (numeric pad keys) */

		default:
			break;
		}
		return Optional.ofNullable(rowBit);
	}
	
	/**
	 * Instantiates a new keyboard decoder.
	 * @param scene 
	 */
	protected JavafxKeyboard() {
		super();
	}
	
	public void keyPressedEventListener(KeyEvent keyEvent) {
		processKeyEvent(keyEvent, true);		
	}
	
	public void keyReleasedEventListener(KeyEvent keyEvent) {
		processKeyEvent(keyEvent, false);		
	}
	
	/**
	 * Process key event.
     *
	 * @param e The key event.
	 * @param state true if key pressed, false if key depressed
	 */
	private void processKeyEvent(KeyEvent e, boolean state) {
		/* Try to match character and translate it to a pressKey/depressKey call */
		int c = e.getCharacter().charAt(0);
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
		
		/* Try to match key and translate it to a pressKey/depressKey call */
		if (!success) {
			Optional<JavafxScreenRowBit> optionalRowBit = rowBitFromKeyCode(e.getCode());
			if (optionalRowBit.isPresent()) {
				JavafxScreenRowBit rowBit = optionalRowBit.get();
				if (state) {
					pressKey(rowBit.row(), rowBit.bit());
				} else {
					depressKey(rowBit.row(), rowBit.bit());
				}
			}
		}
	}

	/**
	 * Set value at given row/bit position to true.
	 *
	 * @param row the row
	 * @param bit the bit
	 */
	private void pressKey(int row, int bit) {
		rows[row] = (byte)((rows[row] & 0xff) | (1 << bit));
	}

	/**
	 * Set value at given row/bit position to false.
	 *
	 * @param row the row
	 * @param bit the bit
	 */
	private void depressKey(int row, int bit) {
		rows[row] = (byte)((rows[row] & 0xff) & ~(1 << bit));
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.keyboard.Keyboard#stopKeyPressed()
	 */
	@Override
	public void stopKeyPressed() {
		pressKey(7, 4);
	}
	
	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.keyboard.Keyboard#stopKeyDepressed()
	 */
	@Override
	public void stopKeyDepressed() {
		depressKey(7, 4);
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.keyboard.Keyboard#getRowValue(int)
	 */
	@Override
	public byte getRowValue(int row) {
		return rows[row];
	}

	/* (non-Javadoc)
	 * @see tmsx.domain.model.hardware.keyboard.Keyboard#setCapslock(boolean)
	 */
	@Override
	public void setCapslock(boolean state) {
//		if (state != Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
//			Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, state);
//		}
	}

	/**
	 * New instance.
	 *
	 * @return the keyboard
	 */
	public static Keyboard newInstance() {
		return new JavafxKeyboard();
	}
	
}
