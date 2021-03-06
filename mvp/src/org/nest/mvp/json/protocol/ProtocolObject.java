//Source file: C:\\svn-pingtai\\longtuRPC\\code\\src\\com\\longtu\\framework\\rpc\\protocol\\ProtocolObject.java

package org.nest.mvp.json.protocol;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * A ProtocolObject is an unordered collection of name/value pairs. Its
 * external form is a string wrapped in curly braces with colons between the
 * names and values, and commas between the values and names. The internal form
 * is an object having get() and opt() methods for accessing the values by name,
 * and put() methods for adding or replacing values by name. The values can be
 * any of these types: Boolean, ProtocolArray, ProtocolObject, Number, String, or the
 * ProtocolObject.NULL object.
 * <p>
 * The constructor can convert an external form string into an internal form
 * Java object. The toString() method creates an external form string.
 * <p>
 * A get() method returns a value if one can be found, and throws an exception
 * if one cannot be found. An opt() method returns a default value instead of
 * throwing an exception, and so is useful for obtaining optional values.
 * <p>
 * The generic get() and opt() methods return an object, which you can cast or
 * query for type. There are also typed get() and opt() methods that do typing
 * checking and type coersion for you.
 * <p>
 * The texts produced by the toString() methods are very strict.
 * The constructors are more forgiving in the texts they will accept.
 * <ul>
 * <li>An extra comma may appear just before the closing brace.</li>
 * <li>Strings may be quoted with single quotes.</li>
 * <li>Strings do not need to be quoted at all if they do not contain leading
 *     or trailing spaces, and if they do not contain any of these characters:
 *     { } [ ] / \ : , </li>
 * <li>Numbers may have the 0- (octal) or 0x- (hex) prefix.</li>
 * </ul>
 * <p>
 * Public Domain 2002 Protocol.org
 * @author Protocol.org
 * @version 0.1
 */
public class ProtocolObject {

    /**
     * ProtocolObject.NULL is equivalent to the value that JavaScript calls null,
     * whilst Java's null is equivalent to the value that JavaScript calls
     * undefined.
     */
     private static final class Null {

        /**
         * Make a Null object.
         */
        private Null() {
        }


        /**
         * There is only intended to be a single instance of the NULL object,
         * so the clone method returns itself.
         * @return     NULL.
         */
        protected final Object clone() {
            return this;
        }


        /**
         * A Null object is equal to the null value and to itself.
         * @param object    An object to test for nullness.
         * @return true if the object parameter is the ProtocolObject.NULL object
         *  or null.
         */
        public boolean equals(Object object) {
            return object == null || object == this;
        }


        /**
         * Get the "null" string value.
         * @return The string "null".
         */
        public String toString() {
            return "null";
        }
    }


    /**
     * The hash map where the ProtocolObject's properties are kept.
     */
    private HashMap myHashMap;

    /**
     * It is sometimes more convenient and less ambiguous to have a NULL
     * object than to use Java's null value.
     * ProtocolObject.NULL.equals(null) returns true.
     * ProtocolObject.NULL.toString() returns "null".
     */
    public static final Object NULL = new Null();

    /**
     * Construct an empty ProtocolObject.
     */
    public ProtocolObject() {
        myHashMap = new HashMap();
    }


    /**
     * Construct a ProtocolObject from a ProtocolTokener.
     * @throws ParseException if there is a syntax error in the source string.
     * @param x A ProtocolTokener object containing the source string.
     */
    public ProtocolObject(ProtocolTokener x) throws ParseException {
        this();
        char c;
        String key;
        if (x.next() == '%') {
            x.unescape();
        }
        x.back();
        if (x.nextClean() != '{') {
            throw x.syntaxError("A ProtocolObject must begin with '{'");
        }
        while (true) {
            c = x.nextClean();
            switch (c) {
            case 0:
                throw x.syntaxError("A ProtocolObject must end with '}'");
            case '}':
                return;
            default:
                x.back();
                key = x.nextValue().toString();
            }
            if (x.nextClean() != ':') {
                throw x.syntaxError("Expected a ':' after a key");
            }
            myHashMap.put(key, x.nextValue());
            switch (x.nextClean()) {
            case ',':
                if (x.nextClean() == '}') {
                    return;
                }
                x.back();
                break;
            case '}':
                return;
            default:
                throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }


    /**
     * Construct a ProtocolObject from a string.
     * @exception ParseException The string must be properly formatted.
     * @param string    A string beginning with '{' and ending with '}'.
     */
    public ProtocolObject(String string) throws ParseException {
        this(new ProtocolTokener(string));
    }


    /**
     * Construct a ProtocolObject from a Map.
     * @param map A map object that can be used to initialize the contents of
     *  the ProtocolObject.
     */
    public ProtocolObject(Map map) {
        myHashMap = new HashMap(map);
    }


    /**
     * Accumulate values under a key. It is similar to the put method except
     * that if there is already an object stored under the key then a
     * ProtocolArray is stored under the key to hold all of the accumulated values.
     * If there is already a ProtocolArray, then the new value is appended to it.
     * In contrast, the put method replaces the previous value.
     * @throws NullPointerException if the key is null
     * @param key   A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     */
    public ProtocolObject accumulate(String key, Object value)
            throws NullPointerException {
        ProtocolArray a;
        Object o = opt(key);
        if (o == null) {
            put(key, value);
        } else if (o instanceof ProtocolArray) {
            a = (ProtocolArray)o;
            a.put(value);
        } else {
            a = new ProtocolArray();
            a.put(o);
            a.put(value);
            put(key, a);
        }
        return this;
    }


    /**
     * Get the value object associated with a key.
     * @exception NoSuchElementException if the key is not found.
     *
     * @param key   A key string.
     * @return      The object associated with the key.
     */
    public Object get(String key) throws NoSuchElementException {
        Object o = opt(key);
        if (o == null) {
            throw new NoSuchElementException("ProtocolObject[" +
                quote(key) + "] not found.");
        }
        return o;
    }


    /**
     * Get the boolean value associated with a key.
     * @exception NoSuchElementException if the key is not found.
     * @exception ClassCastException
     *  if the value is not a Boolean or the String "true" or "false".
     *
     * @param key   A key string.
     * @return      The truth.
     */
    public boolean getBoolean(String key)
            throws ClassCastException, NoSuchElementException {
        Object o = get(key);
        if (o == Boolean.FALSE || o.equals("false")) {
            return false;
        } else if (o == Boolean.TRUE || o.equals("true")) {
            return true;
        }
        throw new ClassCastException("ProtocolObject[" +
            quote(key) + "] is not a Boolean.");
    }


    /**
     * Get the double value associated with a key.
     * @exception NoSuchElementException if the key is not found or
     *  if the value is a Number object.
     * @exception NumberFormatException if the value cannot be converted to a
     *  number.
     * @param key   A key string.
     * @return      The numeric value.
     */
    public double getDouble(String key)
            throws NoSuchElementException, NumberFormatException {
        Object o = get(key);
        if (o instanceof Number) {
            return ((Number)o).doubleValue();
        }
        if (o instanceof String) {
            return new Double((String)o).doubleValue();
        }
        throw new NumberFormatException("ProtocolObject[" +
            quote(key) + "] is not a number.");
    }


    /**
     * Get the HashMap the holds that contents of the ProtocolObject.
     * @return The getHashMap.
     */
     HashMap getHashMap() {
        return myHashMap;
     }


    /**
     * Get the int value associated with a key.
     * @exception NoSuchElementException if the key is not found
     * @exception NumberFormatException
     *  if the value cannot be converted to a number.
     *
     * @param key   A key string.
     * @return      The integer value.
     */
    public int getInt(String key)
            throws NoSuchElementException, NumberFormatException {
        Object o = get(key);
        if (o instanceof Number) {
            return ((Number)o).intValue();
        }
        return (int)getDouble(key);
    }


    /**
     * Get the long value associated with a key.
     * @exception NoSuchElementException if the key is not found
     * @exception NumberFormatException
     *  if the value cannot be converted to a number.
     *
     * @param key   A key string.
     * @return      The long value.
     */
    public long getLong(String key)
            throws NoSuchElementException, NumberFormatException {
        Object o = get(key);
        if (o instanceof Number) {
            return ((Number)o).longValue();
        }
        return (long)getDouble(key);
    }


    /**
     * Get the ProtocolArray value associated with a key.
     * @exception NoSuchElementException if the key is not found or
     *  if the value is not a ProtocolArray.
     *
     * @param key   A key string.
     * @return      A ProtocolArray which is the value.
     */
    public ProtocolArray getProtocolArray(String key) throws NoSuchElementException {
        Object o = get(key);
        if (o instanceof ProtocolArray) {
            return (ProtocolArray)o;
        }
        throw new NoSuchElementException("ProtocolObject[" +
            quote(key) + "] is not a ProtocolArray.");
    }


    /**
     * Get the ProtocolObject value associated with a key.
     * @exception NoSuchElementException if the key is not found or
     *  if the value is not a ProtocolObject.
     *
     * @param key   A key string.
     * @return      A ProtocolObject which is the value.
     */
    public ProtocolObject getProtocolObject(String key) throws NoSuchElementException {
        Object o = get(key);
        if (o instanceof ProtocolObject) {
            return (ProtocolObject)o;
        }
        throw new NoSuchElementException("ProtocolObject[" +
            quote(key) + "] is not a ProtocolObject.");
    }


    /**
     * Get the string associated with a key.
     * @exception NoSuchElementException if the key is not found.
     *
     * @param key   A key string.
     * @return      A string which is the value.
     */
    public String getString(String key) throws NoSuchElementException {
        return get(key).toString();
    }


    /**
     * Determine if the ProtocolObject contains a specific key.
     * @param key   A key string.
     * @return      true if the key exists in the ProtocolObject.
     */
    public boolean has(String key) {
        return myHashMap.containsKey(key);
    }


    /**
     * Determine if the value associated with the key is null or if there is
     *  no value.
     * @param key   A key string.
     * @return      true if there is no value associated with the key or if
     *  the value is the ProtocolObject.NULL object.
     */
    public boolean isNull(String key) {
        return ProtocolObject.NULL.equals(opt(key));
    }


    /**
     * Get an enumeration of the keys of the ProtocolObject.
     *
     * @return An iterator of the keys.
     */
    public Iterator keys() {
        return myHashMap.keySet().iterator();
    }


    /**
     * Get the number of keys stored in the ProtocolObject.
     *
     * @return The number of keys in the ProtocolObject.
     */
    public int length() {
        return myHashMap.size();
    }


    /**
     * Produce a ProtocolArray containing the names of the elements of this
     * ProtocolObject.
     * @return A ProtocolArray containing the key strings, or null if the ProtocolObject
     * is empty.
     */
    public ProtocolArray names() {
        ProtocolArray ja = new ProtocolArray();
        Iterator  keys = keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        if (ja.length() == 0) {
            return null;
        }
        return ja;
    }


    /**
     * Produce a string from a number.
     * @exception ArithmeticException Protocol can only serialize finite numbers.
     * @param  n A Number
     * @return A String.
     */
    static public String numberToString(Number n) throws ArithmeticException {
        if (
                (n instanceof Float &&
                    (((Float)n).isInfinite() || ((Float)n).isNaN())) ||
                (n instanceof Double &&
                    (((Double)n).isInfinite() || ((Double)n).isNaN()))) {
            throw new ArithmeticException(
                "Protocol can only serialize finite numbers.");
        }

// Shave off trailing zeros and decimal point, if possible.

        String s = n.toString().toLowerCase();
        if (s.indexOf('e') < 0 && s.indexOf('.') > 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }


    /**
     * Get an optional value associated with a key.
     * @exception NullPointerException  The key must not be null.
     * @param key   A key string.
     * @return      An object which is the value, or null if there is no value.
     */
    public Object opt(String key) throws NullPointerException {
        if (key == null) {
            throw new NullPointerException("Null key");
        }
        return myHashMap.get(key);
    }


    /**
     * Get an optional boolean associated with a key.
     * It returns false if there is no such key, or if the value is not
     * Boolean.TRUE or the String "true".
     *
     * @param key   A key string.
     * @return      The truth.
     */
    public boolean optBoolean(String key) {
        return optBoolean(key, false);
    }


    /**
     * Get an optional boolean associated with a key.
     * It returns the defaultValue if there is no such key, or if it is not
     * a Boolean or the String "true" or "false".
     *
     * @param key              A key string.
     * @param defaultValue     The default.
     * @return      The truth.
     */
    public boolean optBoolean(String key, boolean defaultValue) {
        Object o = opt(key);
        if (o != null) {
            if (o == Boolean.FALSE || o.equals("false")) {
                return false;
            } else if (o == Boolean.TRUE || o.equals("true")) {
                return true;
            }
        }
        return defaultValue;
    }


    /**
     * Get an optional double associated with a key,
     * or NaN if there is no such key or if its value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A string which is the key.
     * @return      An object which is the value.
     */
    public double optDouble(String key)  {
        return optDouble(key, Double.NaN);
    }


    /**
     * Get an optional double associated with a key, or the
     * defaultValue if there is no such key or if its value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @param defaultValue     The default.
     * @return      An object which is the value.
     */
    public double optDouble(String key, double defaultValue)  {
        Object o = opt(key);
        if (o != null) {
            if (o instanceof Number) {
                return ((Number)o).doubleValue();
            }
            try {
                return new Double((String)o).doubleValue();
            }
            catch (Exception e) {
            }
        }
        return defaultValue;
    }


    /**
     * Get an optional int value associated with a key,
     * or zero if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @return      An object which is the value.
     */
    public int optInt(String key) {
        return optInt(key, 0);
    }


    /**
     * Get an optional int value associated with a key,
     * or the default if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @param defaultValue     The default.
     * @return      An object which is the value.
     */
    public int optInt(String key, int defaultValue) {
        Object o = opt(key);
        if (o != null) {
            if (o instanceof Number) {
                return ((Number)o).intValue();
            }
            try {
                return Integer.parseInt((String)o);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }


    /**
     * Get an optional ProtocolArray associated with a key.
     * It returns null if there is no such key, or if its value is not a
     * ProtocolArray.
     *
     * @param key   A key string.
     * @return      A ProtocolArray which is the value.
     */
    public ProtocolArray optProtocolArray(String key) {
        Object o = opt(key);
        if (o instanceof ProtocolArray) {
            return (ProtocolArray) o;
        }
        return null;
    }


    /**
     * Get an optional ProtocolObject associated with a key.
     * It returns null if there is no such key, or if its value is not a
     * ProtocolObject.
     *
     * @param key   A key string.
     * @return      A ProtocolObject which is the value.
     */
    public ProtocolObject optProtocolObject(String key) {
        Object o = opt(key);
        if (o instanceof ProtocolObject) {
            return (ProtocolObject)o;
        }
        return null;
    }


    /**
     * Get an optional string associated with a key.
     * It returns an empty string if there is no such key. If the value is not
     * a string and is not null, then it is coverted to a string.
     *
     * @param key   A key string.
     * @return      A string which is the value.
     */
    public String optString(String key) {
        return optString(key, "");
    }


    /**
     * Get an optional string associated with a key.
     * It returns the defaultValue if there is no such key.
     *
     * @param key   A key string.
     * @param defaultValue     The default.
     * @return      A string which is the value.
     */
    public String optString(String key, String defaultValue) {
        Object o = opt(key);
        if (o != null) {
            return o.toString();
        }
        return defaultValue;
    }


    /**
     * Put a key/boolean pair in the ProtocolObject.
     *
     * @param key   A key string.
     * @param value A boolean which is the value.
     * @return this.
     */
    public ProtocolObject put(String key, boolean value) {
        put(key, new Boolean(value));
        return this;
    }


    /**
     * Put a key/double pair in the ProtocolObject.
     *
     * @param key   A key string.
     * @param value A double which is the value.
     * @return this.
     */
    public ProtocolObject put(String key, double value) {
        put(key, new Double(value));
        return this;
    }


    /**
     * Put a key/int pair in the ProtocolObject.
     *
     * @param key   A key string.
     * @param value An int which is the value.
     * @return this.
     */
    public ProtocolObject put(String key, int value) {
        put(key, new Integer(value));
        return this;
    }


    /**
     * Put a key/value pair in the ProtocolObject. If the value is null,
     * then the key will be removed from the ProtocolObject if it is present.
     * @exception NullPointerException The key must be non-null.
     * @param key   A key string.
     * @param value An object which is the value. It should be of one of these
     *  types: Boolean, Double, Integer, ProtocolArray, ProtocolObject, String, or the
     *  ProtocolObject.NULL object.
     * @return this.
     */
    public ProtocolObject put(String key, Object value) throws NullPointerException {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            myHashMap.put(key, value);
        } else {
            remove(key);
        }
        return this;
    }


    /**
     * Put a key/value pair in the ProtocolObject, but only if the
     * value is non-null.
     * @exception NullPointerException The key must be non-null.
     * @param key   A key string.
     * @param value An object which is the value. It should be of one of these
     *  types: Boolean, Double, Integer, ProtocolArray, ProtocolObject, String, or the
     *  ProtocolObject.NULL object.
     * @return this.
     */
    public ProtocolObject putOpt(String key, Object value) throws NullPointerException {
        if (value != null) {
            put(key, value);
        }
        return this;
    }


    /**
     * Produce a string in double quotes with backslash sequences in all the
     * right places.
     * @param string A String
     * @return  A String correctly formatted for insertion in a Protocol message.
     */
    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         c;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
            case '/':
                sb.append('\\');
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || c >= 128) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * Remove a name and its value, if present.
     * @param key The name to be removed.
     * @return The value that was associated with the name,
     * or null if there was no value.
     */
    public Object remove(String key) {
        return myHashMap.remove(key);
    }

    /**
     * Produce a ProtocolArray containing the values of the members of this
     * ProtocolObject.
     * @param names A ProtocolArray containing a list of key strings. This
     * determines the sequence of the values in the result.
     * @return A ProtocolArray of values.
     */
    public ProtocolArray toProtocolArray(ProtocolArray names) {
        if (names == null || names.length() == 0) {
            return null;
        }
        ProtocolArray ja = new ProtocolArray();
        for (int i = 0; i < names.length(); i += 1) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }

    /**
     * Make an Protocol external form string of this ProtocolObject. For compactness, no
     * unnecessary whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, portable, transmittable
     *  representation of the object, beginning with '{' and ending with '}'.
     */
    public String toString() {
        Iterator     keys = keys();
        Object       o = null;
        String       s;
        StringBuffer sb = new StringBuffer();

        sb.append('{');
        while (keys.hasNext()) {
            if (o != null) {
                sb.append(',');
            }
            s = keys.next().toString();
            o = myHashMap.get(s);
            if (o != null) {
                sb.append(quote(s));
                sb.append(':');
                if (o instanceof String) {
                    sb.append(quote((String)o));
                } else if (o instanceof Number) {
                    sb.append(numberToString((Number)o));
                } else {
                    sb.append(o.toString());
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }


    /**
     * Make a prettyprinted Protocol external form string of this ProtocolObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @return a printable, displayable, portable, transmittable
     *  representation of the object, beginning with '{' and ending with '}'.
     */
    public String toString(int indentFactor) {
        return toString(indentFactor, 0);
    }


    /**
     * Make a prettyprinted Protocol string of this ProtocolObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning with '{' and ending with '}'.
     */
    String toString(int indentFactor, int indent) {
        int          i;
        Iterator     keys = keys();
        String       pad = "";
        StringBuffer sb = new StringBuffer();
        indent += indentFactor;
        for (i = 0; i < indent; i += 1) {
            pad += ' ';
        }
        sb.append("{\n");
        while (keys.hasNext()) {
            String s = keys.next().toString();
            Object o = myHashMap.get(s);
            if (o != null) {
                if (sb.length() > 2) {
                    sb.append(",\n");
                }
                sb.append(pad);
                sb.append(quote(s));
                sb.append(": ");
                if (o instanceof String) {
                    sb.append(quote((String)o));
                } else if (o instanceof Number) {
                    sb.append(numberToString((Number) o));
                } else if (o instanceof ProtocolObject) {
                    sb.append(((ProtocolObject)o).toString(indentFactor, indent));
                } else if (o instanceof ProtocolArray) {
                    sb.append(((ProtocolArray)o).toString(indentFactor, indent));
                } else {
                    sb.append(o.toString());
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
