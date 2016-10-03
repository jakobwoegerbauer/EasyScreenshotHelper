/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyscreenshothelper;

/**
 *
 * @author Simon
 */
public class ObjectWrapper<T> {
	private T value;
	
	public ObjectWrapper(T initialValue) {
		this.value = initialValue;
	}
	public ObjectWrapper() {
		this(null);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
