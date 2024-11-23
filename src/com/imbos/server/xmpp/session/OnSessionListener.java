package com.imbos.server.xmpp.session;

public interface OnSessionListener {
	public void onCreate(ClientSession clientSession);
	public void onClose(ClientSession clientSession);
}
