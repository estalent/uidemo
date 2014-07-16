package com.wasu.android.rainbowbox.aidl;

import com.wasu.android.rainbowbox.aidl.WasuUserCallback;

interface WasuUserInterface {
	void checkIsVIP(WasuUserCallback callback);
	void registerWasuVIP(String taccount, String zaccount, String tphone, String temail, WasuUserCallback callback);
}