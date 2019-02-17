package com.taist.ssl.fragment;

import com.taist.ssl.Fragment;

public abstract class AbstractFragment implements Fragment {
	protected byte[] data = null;
	public AbstractFragment(byte[] data) {
		this.data = data;
	}
	
	@Override
	public byte[] getBytes() {
		return data;
	}
}
