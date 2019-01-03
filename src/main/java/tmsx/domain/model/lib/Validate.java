/**
 * 
 */
package tmsx.domain.model.lib;

// TODO: Auto-generated Javadoc
/**
 * The Class ValidateState.
 *
 * @author eduardo
 */
public class Validate {

	/**
	 * Checks if is empty string.
	 *
	 * @param value the value
	 * @return true, if is empty string
	 */
	public static boolean isEmptyString(String value){
		return (value == null || "".equals(value));
	}

	/**
	 * Checks if is empty string.
	 *
	 * @param value the value
	 * @return true, if is empty string
	 */
	public static boolean isNotEmptyString(String value){
		return !isEmptyString(value);
	}

	/**
	 * Checks if is null.
	 *
	 * @param value the value
	 * @return true, if is null
	 */
	public static boolean isNull(Object value){
		return (value == null);
	}

	/**
	 * Checks if is not null.
	 *
	 * @param value the value
	 * @return true, if is not null
	 */
	public static boolean isNotNull(Object value){
		return (value != null);
	}
	
	/**
	 * Checks if is int zero.
	 *
	 * @param value the value
	 * @return true, if is int zero
	 */
	public static boolean isIntZero(Integer value){
		return (value == 0);
	}

	/**
	 * Checks if is int positive.
	 *
	 * @param value the value
	 * @return true, if is int positive
	 */
	public static boolean isIntPositive(Integer value){
		return (value != null) && (value > 0);
	}

	/**
	 * Checks if is int negative.
	 *
	 * @param value the value
	 * @return true, if is int negative
	 */
	public static boolean isIntNegative(Integer value){
		return (value != null) && (value < 0);
	}
	
	/**
	 * String not empty.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void stringNotEmpty(String msg, String value){
		if (isEmptyString(value)) throw new IllegalStateException(msg);
	}

	/**
	 * Condition.
	 *
	 * @param msg the msg
	 * @param condition the condition
	 */
	public static void condition(String msg, boolean condition){
		if (condition) throw new IllegalStateException(msg);
	}
	
	/**
	 * Not null.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void notNull(String msg, Object value){
		if (isNull(value)) throw new IllegalStateException(msg);
	}

	/**
	 * Check null.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void checkNull(String msg, Object value){
		if (!isNull(value)) throw new IllegalStateException(msg);
	}

	/**
	 * Checks if is true.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void isTrue(String msg, boolean value){
		if (!value) throw new IllegalStateException(msg);
	}

	/**
	 * Checks if is false.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void isFalse(String msg, boolean value){
		if (value) throw new IllegalStateException(msg);
	}
	
	/**
	 * Integer not zero.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void intNotZero(String msg, int value){
		if (isIntZero(value)) throw new IllegalStateException(msg);
	}

	/**
	 * Checks if is int neither null nor zero.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void isIntNeitherNullNorZero(String msg, Integer value){
		notNull(msg, value);
		if (value == 0) throw new IllegalStateException(msg);
	}

	/**
	 * Int neither null nor zero.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void intNeitherNullNorZero(String msg, Integer value){
		notNull(msg, value);
		if (value == 0) throw new IllegalStateException(msg);
	}

	/**
	 * Greater.
	 *
	 * @param msg the msg
	 * @param value the value
	 * @param base the base
	 */
	public static void greater(String msg, Integer value, Integer base){
		notNull(msg, value);
		notNull(String.format("%s (BASE)", msg), base);
		if (value <= base) throw new IllegalStateException(msg);
	}
	
	/**
	 * Positive.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void positive(String msg, Integer value){
		notNull(msg, value);
		if (value <= 0) throw new IllegalStateException(msg);
	}

	/**
	 * Positive.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void positiveOrZero(String msg, Integer value){
		notNull(msg, value);
		if (value < 0) throw new IllegalStateException(msg);
	}

	/**
	 * Negative.
	 *
	 * @param msg the msg
	 * @param value the value
	 */
	public static void negative(String msg, Integer value){
		notNull(msg, value);
		if (value >= 0) throw new IllegalStateException(msg);
	}
	
	/**
	 * Custom.
	 *
	 * @param msg the msg
	 * @param value the value
	 * @param validator the validator
	 */
	public static void custom(String msg, Integer value, Validator validator){
		if (!validator.isValid(value)) throw new IllegalStateException(String.format(msg, value));
	}
	
	/**
	 * The Interface Valid.
	 */
	public interface Validator {
		
		/**
		 * Checks if is valid.
		 *
		 * @param value the value
		 * @return true, if is valid
		 */
		public boolean isValid(Object value);
		
	}
	
}
