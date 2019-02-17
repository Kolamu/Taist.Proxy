package com.taist.ssl.fragment;

import com.taist.helper.ByteHelper;

public class CertificateDataFragment extends AbstractFragment {
	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	private int length = 0;
	private byte[] certificate = null;
	public CertificateDataFragment(byte[] data) {
		super(data);
	}

	private void parse(){
		length = ByteHelper.toInt(data, 0, 3);
		certificate = ByteHelper.subarray(data, 3, data.length - 3);
	}
}
