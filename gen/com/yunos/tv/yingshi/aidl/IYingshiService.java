/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\for yuan kai\\AliYingshi\\src\\com\\yunos\\tv\\yingshi\\aidl\\IYingshiService.aidl
 */
package com.yunos.tv.yingshi.aidl;
public interface IYingshiService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yunos.tv.yingshi.aidl.IYingshiService
{
private static final java.lang.String DESCRIPTOR = "com.yunos.tv.yingshi.aidl.IYingshiService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yunos.tv.yingshi.aidl.IYingshiService interface,
 * generating a proxy if needed.
 */
public static com.yunos.tv.yingshi.aidl.IYingshiService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yunos.tv.yingshi.aidl.IYingshiService))) {
return ((com.yunos.tv.yingshi.aidl.IYingshiService)iin);
}
return new com.yunos.tv.yingshi.aidl.IYingshiService.Stub.Proxy(obj);
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
case TRANSACTION_getLastPlayList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List _result = this.getLastPlayList();
reply.writeNoException();
reply.writeList(_result);
return true;
}
case TRANSACTION_getCategoryList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.util.Map _result = this.getCategoryList(_arg0);
reply.writeNoException();
reply.writeMap(_result);
return true;
}
case TRANSACTION_getCacheCatatoryList:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _result = this.getCacheCatatoryList();
reply.writeNoException();
reply.writeMap(_result);
return true;
}
case TRANSACTION_getDianboList:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _result = this.getDianboList();
reply.writeNoException();
reply.writeMap(_result);
return true;
}
case TRANSACTION_getMessageList:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _result = this.getMessageList();
reply.writeNoException();
reply.writeMap(_result);
return true;
}
case TRANSACTION_getPlayBackList:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _result = this.getPlayBackList();
reply.writeNoException();
reply.writeMap(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yunos.tv.yingshi.aidl.IYingshiService
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
@Override public java.util.List getLastPlayList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLastPlayList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readArrayList(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.Map getCategoryList(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_getCategoryList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.Map getCacheCatatoryList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCacheCatatoryList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.Map getDianboList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDianboList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.Map getMessageList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMessageList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.Map getPlayBackList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPlayBackList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getLastPlayList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getCategoryList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getCacheCatatoryList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getDianboList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getMessageList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getPlayBackList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public java.util.List getLastPlayList() throws android.os.RemoteException;
public java.util.Map getCategoryList(int type) throws android.os.RemoteException;
public java.util.Map getCacheCatatoryList() throws android.os.RemoteException;
public java.util.Map getDianboList() throws android.os.RemoteException;
public java.util.Map getMessageList() throws android.os.RemoteException;
public java.util.Map getPlayBackList() throws android.os.RemoteException;
}
