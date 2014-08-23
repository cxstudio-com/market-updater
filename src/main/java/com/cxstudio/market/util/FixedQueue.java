package com.cxstudio.market.util;

import java.util.LinkedList;

public class FixedQueue<E> extends LinkedList<E> {
	private static final long serialVersionUID = 1L;
	private int limit;

	public FixedQueue(int limit) {
		this.limit = limit;
	}

	@Override
	public boolean add(E o) {
		super.add(o);
		if (this.size() > limit) {
			this.remove();
		}
		return true;
	}
}
