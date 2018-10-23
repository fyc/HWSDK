package com.yiyou.gamesdk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NativeTitleBarUpdateInfo implements Parcelable{

	public boolean showBackButton ;
//	public boolean showRefreshButton ;
	public boolean showCloseButton ;
	public boolean showConfirmButton;

	public NativeTitleBarUpdateInfo() {
	}

	protected NativeTitleBarUpdateInfo(Parcel in) {
		showBackButton = in.readInt() == 1;
//		showRefreshButton = in.readInt() == 1;
		showCloseButton = in.readInt() == 1;
		showConfirmButton = in.readInt() == 1;
	}

	public static final Creator<NativeTitleBarUpdateInfo> CREATOR = new Creator<NativeTitleBarUpdateInfo>() {
		@Override
		public NativeTitleBarUpdateInfo createFromParcel(Parcel in) {
			return new NativeTitleBarUpdateInfo(in);
		}

		@Override
		public NativeTitleBarUpdateInfo[] newArray(int size) {
			return new NativeTitleBarUpdateInfo[size];
		}
	};

	@Override
	public String toString() {
		return "NativeTitleBarUpdateInfo{" +
				"showBackButton=" + showBackButton +
//				", showRefreshButton=" + showRefreshButton +
				", showCloseButton=" + showCloseButton +
				", showConfirmButton=" + showConfirmButton +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(showBackButton ? 1 : 0);
//		dest.writeInt(showRefreshButton ? 1 : 0);
		dest.writeInt(showCloseButton ? 1 : 0);
		dest.writeInt(showConfirmButton ? 1 : 0);
	}
}
