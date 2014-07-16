package net.zhilink.wasualiplayer.services;

import net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback;

interface IWasuAliPlayerService {
	void queryPrice(String jsonString, IWasuAliPlayerCallback cb);
}