/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\for yuan kai\\AliYingshi\\src\\com\\wasu\\android\\rainbowbox\\aidl\\WasuUserInterface.aidl
 */
package com.wasu.android.rainbowbox.aidl;
public interface WasuUserInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.wasu.android.rainbowbox.aidl.WasuUserInterface
{
private static final java.lang.String DESCRIPTOR = "com.wasu.android.rainbowbox.aidl.WasuUserInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.wasu.android.rainbowbox.aidl.WasuUserInterface interface,
 * generating a proxy if needed.
 */
public static com.wasu.android.rainbowbox.aidl.WasuUserInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.wasu.android.rainbowbox.aidl.WasuUserInterface))) {
return ((com.wasu.android.rainbowbox.aidl.WasuUserInterface)iin);
}
return new com.wasu.android.rainbowbox.aidl.WasuUserInterface.Stub.Proxy(obj);
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
case TRANSACTION_checkIsVIP:
{
data.enforceInterface(DESCRIPTOR);
com.wasu.android.rainbowbox.aidl.WasuUserCallback _arg0;
_arg0 = com.wasu.android.rainbowbox.aidl.WasuUserCallback.Stub.asInterface(data.readStrongBinder());
this.checkIsVIP(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerWasuVIP:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
com.wasu.android.rainbowbox.aidl.WasuUserCallback _arg4;
_arg4 = com.wasu.android.rainbowbox.aidl.WasuUserCallback.Stub.asInterface(data.readStrongBinder());
this.registerWasuVIP(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.wasu.android.rainbowbox.aidl.WasuUserInterface
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
@Override public void checkIsVIP(com.wasu.android.rainbowbox.aidl.WasuUserCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_checkIsVIP, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerWasuVIP(java.lang.String taccount, java.lang.String zaccount, java.lang.String tphone, java.lang.String temail, com.wasu.android.rainbowbox.aidl.WasuUserCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(taccount);
_data.writeString(zaccount);
_data.writeString(tphone);
_data.writeString(temail);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerWasuVIP, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_checkIsVIP = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_registerWasuVIP = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void checkIsVIP(com.wasu.android.rainbowbox.aidl.WasuUserCallback callback) throws android.os.RemoteException;
public void registerWasuVIP(java.lang.String taccount, java.lang.String zaccount, java.lang.String tphone, java.lang.String temail, com.wasu.android.rainbowbox.aidl.WasuUserCallback callback) throws android.os.RemoteException;
}
