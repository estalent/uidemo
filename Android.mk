LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES:=alitv_yingshi_lib_android-support-v4:libs/android-support-v4.jar \
			alitv_yingshi_lib_commons-codec:libs/commons-codec-1.6.jar \
			alitv_yingshi_lib_gson-2.2.2:libs/gson-2.2.2.jar \
			alitv_yingshi_lib_commons-httpclient:libs/commons-httpclient-3.0-rc2.jar \
			alitv_yingshi_lib_httpmime:libs/httpmime-4.2.3.jar \
			alitv_yingshi_lib_imageload:lib/imageload.jar \
			alitv_yingshi_lib_lib_base:lib/lib_base.jar \
			alitv_yingshi_lib_alitvsdk:lib/AliTvAppSdk.jar \
			alitv_yingshi_lib_tyid:libs/tyid.jar \
			
			
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)

LOCAL_CERTIFICATE := platform
LOCAL_PACKAGE_NAME := AliTvYingshi
LOCAL_MODULE_TAGS := optional

LOCAL_JAVA_LIBRARIES:= AuiJar

LOCAL_STATIC_JAVA_LIBRARIES :=alitv_yingshi_lib_android-support-v4 \
					alitv_yingshi_lib_commons-codec \
					alitv_yingshi_lib_gson-2.2.2 \
					alitv_yingshi_lib_commons-httpclient \
					alitv_yingshi_lib_httpmime \
					alitv_yingshi_lib_imageload \
					alitv_yingshi_lib_lib_base \
					alitv_yingshi_lib_alitvsdk \
					alitv_yingshi_lib_tyid


LOCAL_SRC_FILES := $(call all-java-files-under, src) \
						src/com/yunos/tv/yingshi/aidl/IYingshiService.aidl \
						src/com/wasu/android/rainbowbox/aidl/WasuUserCallback.aidl \
						src/com/wasu/android/rainbowbox/aidl/WasuUserInterface.aidl \
						src/net/zhilink/wasualiplayer/services/IWasuAliPlayerCallback.aidl \
						src/net/zhilink/wasualiplayer/services/IWasuAliPlayerService.aidl
						

LOCAL_PROGUARD_FLAGS := -include $(TOPDIR)build/target/product/proguard.cfg
LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)
