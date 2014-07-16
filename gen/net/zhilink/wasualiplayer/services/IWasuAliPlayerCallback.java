/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\for yuan kai\\AliYingshi\\src\\net\\zhilink\\wasualiplayer\\services\\IWasuAliPlayerCallback.aidl
 */
package net.zhilink.wasualiplayer.services;
public interface IWasuAliPlayerCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback
{
private static final java.lang.String DESCRIPTOR = "net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback interface,
 * generating a proxy if needed.
 */
public static net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback))) {
return ((net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback)iin);
}
return new net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onResultPrice:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onResultPrice(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void onResultPrice(java.lang.String jsonString) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jsonString);
mRemote.transact(Stub.TRANSACTION_onResultPrice, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onResultPrice = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void onResultPrice(java.lang.String jsonString) throws android.os.RemoteException;
}
