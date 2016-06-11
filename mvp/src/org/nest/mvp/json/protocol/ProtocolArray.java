//Source file: C:\\svn-pingtai\\longtuRPC\\code\\src\\com\\longtu\\framework\\rpc\\protocol\\ProtocolArray.java

package org.nest.mvp.json.protocol;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * A ProtocolArray is an ordered sequence of values. Its external form is a string
 * wrapped in square brackets with commas between the values. The internal form
 * is an object having get() and opt() methods for accessing the values by
 * index, and put() methods for adding or replacing values. The values can be
 * any of these types: Boolean, ProtocolArray, ProtocolObject, Number, String, or the
 * ProtocolObject.NULL object.
 * <p>
 * The constructor can convert a Protocol external form string into an
 * internal form Java object. The toString() method creates an external
 * form string.
 * <p>
 * A get() method returns a value if one can be found, and throws an exception
 * if one cannot be found. An opt() method returns a default value instead of
 * throwing an exception, and so is useful for obtaining optional values.
 * <p>
 * The generic get() and opt() methods return an object which you can cast or
 * query for type. There are also typed get() and opt() methods that do typing
 * checking and type coersion for you.
 * <p>
 * The texts produced by the toString() methods are very strict.
 * The constructors are more forgiving in the texts they will accept.
 * <ul>
 * <li>An extra comma may appear just before the closing bracket.</li>
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


public class ProtocolArray {


    /**
     * The getArrayList where the ProtocolArray's properties are kept.
     */
    private ArrayList myArrayList;


    /**
     * Construct an empty ProtocolArray.
     */
    public ProtocolArray() {
        myArrayList = new ArrayList();
    }


    /**
     * Construct a ProtocolArray from a ProtocolTokener.
     * @exception ParseException A ProtocolArray must start with '['
     * @exception ParseException Expected a ',' or ']'
     * @param x A ProtocolTokener
     */
    public ProtocolArray(ProtocolTokener x) throws ParseException {
        this();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A ProtocolArray must start with '['");
        }
        if (x.nextClean() == ']') {
            return;
        }
        x.back();
        while (true) {
            myArrayList.add(x.nextValue());
            switch (x.nextClean()) {
            case ',':
                if (x.nextClean() == ']') {
                    return;
                }
                x.back();
                break;
            case ']':
                return;
            default:
                throw x.syntaxError("Expected a ',' or ']'");
            }
        }
    }


    /**
     * Construct a ProtocolArray from a source string.
     * @exception ParseException The string must conform to Protocol syntax.
     * @param string     A string that begins with '[' and ends with ']'.
     */
    public ProtocolArray(String string) throws ParseException {
        this(new ProtocolTokener(string));
    }


    /**
     * Construct a ProtocolArray from a Collection.
     * @param collection     A Collection.
     */
    public ProtocolArray(Collection collection) {
        myArrayList = new ArrayList(collection);
    }


    /**
     * Get the object value associated with an index.
     * @exception NoSuchElementException
     * @param index subscript
     *  The index must be between 0 and length() - 1.
     * @return An object value.
     */
    public Object get(int index) throws NoSuchElementException {
        Object o = opt(index);
        if (o == null) {
            throw new NoSuchElementException("ProtocolArray[" + index +
                "] not found.");
        }
        return o;
    }


    /**
     * Get the ArrayList which is holding the elements of the ProtocolArray.
     * @return      The ArrayList.
     */
    ArrayList getArrayList() {
        return myArrayList;
    }


    /**
     * Get the boolean value associated with an index.
     * The string values "true" and "false" are converted to boolean.
     *
     * @exception NoSuchElementException if the index is not found
     * @exception ClassCastException
     * @param index subscript
     * @return      The truth.
     */
    public boolean getBoolean(int index)
            throws ClassCastException, NoSuchElementException {
        Object o = get(index);
        if (o == Boolean.FALSE || o.equals("false")) {
            return false;
        } else if (o == Boolean.TRUE || o.equals("true")) {
            return true;
        }
        throw new ClassCastException("ProtocolArray[" + index +
            "] not a Boolean.");
    }


    /**
     * Get the double value associated with an index.
     * @exception NoSuchElementException if the key is not found
     * @exception NumberFormatException
     *  if the value cannot be converted to a number.
     *
     * @param index subscript
     * @return      The value.
     */
    public double getDouble(int index)
            throws NoSuchElementException, NumberFormatException {
        Object o = get(index);
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        if (o instanceof String) {
            return new Double((String)o).doubleValue();
        }
        throw new NumberFormatException("ProtocolObject[" +
            index + "] is not a number.");
    }


    /**
     * Get the int value associated with an index.
     * @exception NoSuchElementException if the key is not found
     * @exception NumberFormatException
     *  if the value cannot be converted to a number.
     *
     * @param index subscript
     * @return      The value.
     */
    public int getInt(int index)
            throws NoSuchElementException, NumberFormatException {
        Object o = get(index);
        if (o instanceof Number) {
            return ((Number)o).intValue();
        }
        return (int)getDouble(index);
    }


    /**
     * Get the ProtocolArray associated with an index.
     * @exception NoSuchElementException if the index is not found or if the
     * value is not a ProtocolArray
     * @param index subscript
     * @return      A ProtocolArray value.
     */
    public ProtocolArray getProtocolArray(int index) throws NoSuchElementException {
        Object o = get(index);
        if (o instanceof ProtocolArray) {
            return (ProtocolArray)o;
        }
        throw new NoSuchElementException("ProtocolArray[" + index +
                "] is not a ProtocolArray.");
    }


    /**
     * Get the ProtocolObject associated with an index.
     * @exception NoSuchElementException if the index is not found or if the
     * value is not a ProtocolObject
     * @param index subscript
     * @return      A ProtocolObject value.
     */
    public ProtocolObject getProtocolObject(int index) throws NoSuchElementException {
        Object o = get(index);
        if (o instanceof ProtocolObject) {
            return (ProtocolObject)o;
        }
        throw new NoSuchElementException("ProtocolArray[" + index +
            "] is not a ProtocolObject.");
    }


    /**
     * Get the string associated with an index.
     * @exception NoSuchElementException
     * @param index subscript
     * @return      A string value.
     */
    public String getString(int index) throws NoSuchElementException {
        return get(index).toString();
    }


    /**
     * Determine if the value is null.
     * @param index subscript
     * @return true if the value at the index is null, or if there is no value.
     */
    public boolean isNull(int index) {
        Object o = opt(index);
        return o == null || o.equals(null);
    }


    /**
     * Make a string from the contents of this ProtocolArray. The separator string
     * is inserted between each element.
     * Warning: This method assumes that the data structure is acyclical.
     * @param separator A string that will be inserted between the elements.
     * @return a string.
     */
    public String join(String separator) {
        int i;
        Object o;
        StringBuffer sb = new StringBuffer();
        for (i = 0; i < myArrayList.size(); i += 1) {
            if (i > 0) {
                sb.append(separator);
            }
            o = myArrayList.get(i);
            if (o == null) {
                sb.append("null");
            } else if (o instanceof String) {
                sb.append(ProtocolObject.quote((String)o));
            } else if (o instanceof Number) {
                sb.append(ProtocolObject.numberToString((Number)o));
            } else {
                sb.append(o.toString());
            }
        }
        return sb.toString();
    }


    /**
     * Get the length of the ProtocolArray.
     *
     * @return The length (or size).
     */
    public int length() {
        return myArrayList.size();
    }


    /**
     * Get the optional object value associated with an index.
     * @param index subscript
     * @return      An object value, or null if there is no
     *              object at that index.
     */
    public Object opt(int index) {
        if (index < 0 || index >= length()) {
            return null;
        } else {
            return myArrayList.get(index);
        }
    }


    /**
     * Get the optional boolean value associated with an index.
     * It returns false if there is no value at that index,
     * or if the value is not Boolean.TRUE or the String "true".
     *
     * @param index subscript
     * @return      The truth.
     */
    public boolean optBoolean(int index)  {
        return optBoolean(index, false);
    }


    /**
     * Get the optional boolean value associated with an index.
     * It returns the defaultValue if there is no value at that index or if it is not
     * a Boolean or the String "true" or "false".
     *
     * @param index subscript
     * @param defaultValue     A boolean default.
     * @return      The truth.
     */
    public boolean optBoolean(int index, boolean defaultValue)  {
        Object o = opt(index);
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
     * Get the optional double value associated with an index.
     * NaN is returned if the index is not found,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index subscript
     * @return      The value.
     */
    public double optDouble(int index) {
        return optDouble(index, Double.NaN);
    }


    /**
     * Get the optional double value associated with an index.
     * The defaultValue is returned if the index is not found,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index subscript
     * @param defaultValue     The default value.
     * @return      The value.
     */
    public double optDouble(int index, double defaultValue) {
        Object o = opt(index);
        if (o != null) {
            if (o instanceof Number) {
                return ((Number) o).doubleValue();
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
     * Get the optional int value associated with an index.
     * Zero is returned if the index is not found,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index subscript
     * @return      The value.
     */
    public int optInt(int index) {
        return optInt(index, 0);
    }


    /**
     * Get the optional int value associated with an index.
     * The defaultValue is returned if the index is not found,
     * or if the value is not a number and cannot be converted to a number.
     * @param index subscript
     * @param defaultValue     The default value.
     * @return      The value.
     */
    public int optInt(int index, int defaultValue) {
        Object o = opt(index);
        if (o != null) {
            if (o instanceof Number) {
                return ((Number)o).intValue();
            }
            try {
                return Integer.parseInt((String)o);
            }
            catch (Exception e) {
            }
        }
        return defaultValue;
    }


    /**
     * Get the optional ProtocolArray associated with an index.
     * @param index subscript
     * @return      A ProtocolArray value, or null if the index has no value,
     * or if the value is not a ProtocolArray.
     */
    public ProtocolArray optProtocolArray(int index) {
        Object o = opt(index);
        if (o instanceof ProtocolArray) {
            return (ProtocolArray)o;
        }
        return null;
    }


    /**
     * Get the optional ProtocolObject associated with an index.
     * Null is returned if the key is not found, or null if the index has
     * no value, or if the value is not a ProtocolObject.
     *
     * @param index subscript
     * @return      A ProtocolObject value.
     */
    public ProtocolObject optProtocolObject(int index) {
        Object o = opt(index);
        if (o instanceof ProtocolObject) {
            return (ProtocolObject)o;
        }
        return null;
    }


    /**
     * Get the optional string value associated with an index. It returns an
     * empty string if there is no value at that index. If the value
     * is not a string and is not null, then it is coverted to a string.
     *
     * @param index subscript
     * @return      A String value.
     */
    public String optString(int index){
        return optString(index, "");
    }


    /**
     * Get the optional string associated with an index.
     * The defaultValue is returned if the key is not found.
     *
     * @param index subscript
     * @param defaultValue     The default value.
     * @return      A String value.
     */
    public String optString(int index, String defaultValue){
        Object o = opt(index);
        if (o != null) {
            return o.toString();
        }
        return defaultValue;
    }


    /**
     * Append a boolean value.
     *
     * @param value A boolean value.
     * @return this.
     */
    public ProtocolArray put(boolean value) {
        put(new Boolean(value));
        return this;
    }


    /**
     * Append a double value.
     *
     * @param value A double value.
     * @return this.
     */
    public ProtocolArray put(double value) {
        put(new Double(value));
        return this;
    }


    /**
     * Append an int value.
     *
     * @param value An int value.
     * @return this.
     */
    public ProtocolArray put(int value) {
        put(new Integer(value));
        return this;
    }


    /**
     * Append an object value.
     * @param value An object value.  The value should be a
     *  Boolean, Double, Integer, ProtocolArray, JSObject, or String, or the
     *  ProtocolObject.NULL object.
     * @return this.
     */
    public ProtocolArray put(Object value) {
        myArrayList.add(value);
        return this;
    }


    /**
     * Put or replace a boolean value in the ProtocolArray.
     * @exception NoSuchElementException The index must not be negative.
     * @param index subscript The subscript. If the index is greater than the length of
     *  the ProtocolArray, then null elements will be added as necessary to pad
     *  it out.
     * @param value A boolean value.
     * @return this.
     */
    public ProtocolArray put(int index, boolean value) {
        put(index, new Boolean(value));
        return this;
    }


    /**
     * Put or replace a double value.
     * @exception NoSuchElementException The index must not be negative.
     * @param index subscript The subscript. If the index is greater than the length of
     *  the ProtocolArray, then null elements will be added as necessary to pad
     *  it out.
     * @param value A double value.
     * return this.
     */
    public ProtocolArray put(int index, double value) {
        put(index, new Double(value));
        return this;
    }


    /**
     * Put or replace an int value.
     * @exception NoSuchElementException The index must not be negative.
     * @param index subscript The subscript. If the index is greater than the length of
     *  the ProtocolArray, then null elements will be added as necessary to pad
     *  it out.
     * @param value An int value.
     * @return this.
     */
    public ProtocolArray put(int index, int value) {
        put(index, new Integer(value));
        return this;
    }


    /**
     * Put or replace an object value in the ProtocolArray.
     * @exception NoSuchElementException The index must not be negative.
     * @param index The subscript. If the index is greater than the length of
     *  the ProtocolArray, then null elements will be added as necessary to pad
     *  it out.
     * @param value An object value.
     * return this.
     */
    public ProtocolArray put(int index, Object value)
            throws NoSuchElementException, NullPointerException {
        if (index < 0) {
            throw new NoSuchElementException("ProtocolArray[" + index +
                "] not found.");
        } else if (value == null) {
            throw new NullPointerException();
        } else if (index < length()) {
            myArrayList.set(index, value);
        } else {
            while (index != length()) {
                put(null);
            }
            put(value);
        }
        return this;
    }


    /**
     * Produce a ProtocolObject by combining a ProtocolArray of names with the values
     * of this ProtocolArray.
     * @param names A ProtocolArray containing a list of key strings. These will be
     * paired with the values.
     * @return A ProtocolObject, or null if there are no names or if this ProtocolArray
     * has no values.
     */
    public ProtocolObject toProtocolObject(ProtocolArray names) {
        if (names == null || names.length() == 0 || length() == 0) {
            return null;
        }
        ProtocolObject jo = new ProtocolObject();
        for (int i = 0; i < names.length(); i += 1) {
            jo.put(names.getString(i), this.opt(i));
        }
        return jo;
    }


    /**
     * Make an Protocol external form string of this ProtocolArray. For compactness, no
     * unnecessary whitespace is added.
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, transmittable
     *  representation of the array.
     */
    public String toString() {
        return '[' + join(",") + ']';
    }


    /**
     * Make a prettyprinted Protocol string of this ProtocolArray.
     * Warning: This method assumes that the data structure is non-cyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning with '[' and ending with ']'.
     */
    public String toString(int indentFactor) {
        return toString(indentFactor, 0);
    }


    /**
     * Make a prettyprinted string of this ProtocolArray.
     * Warning: This method assumes that the data structure is non-cyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indention of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the array.
     */
    String toString(int indentFactor, int indent) {
        int i;
        Object o;
        String pad = "";
        StringBuffer sb = new StringBuffer();
        indent += indentFactor;
        for (i = 0; i < indent; i += 1) {
            pad += ' ';
        }
        sb.append("[\n");
        for (i = 0; i < myArrayList.size(); i += 1) {
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append(pad);
            o = myArrayList.get(i);
            if (o == null) {
                sb.append("null");
            } else if (o instanceof String) {
                sb.append(ProtocolObject.quote((String) o));
            } else if (o instanceof Number) {
                sb.append(ProtocolObject.numberToString((Number) o));
            } else if (o instanceof ProtocolObject) {
                sb.append(((ProtocolObject)o).toString(indentFactor, indent));
            } else if (o instanceof ProtocolArray) {
                sb.append(((ProtocolArray)o).toString(indentFactor, indent));
            } else {
                sb.append(o.toString());
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
