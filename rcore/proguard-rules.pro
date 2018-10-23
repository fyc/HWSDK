# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\WORK\adt-bundle-windows-x86_64\androidSDK\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class gameName to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-printmapping rcore-mapping.map
-keep  class com.yiyou.gamesdk.container.**{
  *;
}
-keep  class com.yiyou.gamesdk.util.**{
  *;
}
-keepattributes Signature

-keep public class com.yiyou.gamesdk.core.CoreManager{
  public <methods>;
}

-keep  class com.android.volley1.**{*;}
-keep public class com.yiyou.gamesdk.model.**{*;}
-keep public class com.yiyou.gamesdk.core.base.web.jsi.TTSDKJSAPI{*;}
-keep public class com.yiyou.gamesdk.core.base.web.jsi.TTCompactJSAPI{*;}
-keep public class com.yiyou.gamesdk.rcore.RSDKImpl{
  public <methods>;
}
-keep public class com.yiyou.gamesdk.core.ui.fragment.**{
}
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-dontskipnonpubliclibraryclasses

#支付宝jar包
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

##聚合现在支付
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.auth.AlipaySDK{ public *;}
-keep class com.alipay.sdk.auth.APAuthInfo{ public *;}
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.ut.*
-keep class cn.gov.pbc.tsm.*{*;}
-keep class com.UCMobile.PayPlugin.*{*;}
-keep class com.unionpay.**{*;}
-dontwarn com.unionpay.**
-keep public class com.ipaynow.plugin.utils.MerchantTools{
          <fields>;
          <methods>;
      }
-keep class cn.gov.pbc.tsm.*{*;}
-keep class com.UCMobile.PayPlugin.*{*;}
-keep class com.unionpay.*{*;}
-dontwarn com.unionpay.**
-keep class com.yiyou.gamesdk.util.MetaUtil{*;}
