package com.gitee.cnsukidayo.anylanguageword.entity.waper;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.gitee.cnsukidayo.anylanguageword.entity.UserCreditStyle;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditFilter;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditFormat;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditOrder;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditState;

/**
 * 背诵风格
 *
 * @author cnsukidayo
 * @date 2023/2/9 19:59
 */
public class UserCreditStyleWrapper implements Parcelable {

    private UserCreditStyle userCreditStyle;

    public UserCreditStyleWrapper(UserCreditStyle userCreditStyle) {
        this.userCreditStyle = userCreditStyle;
    }

    protected UserCreditStyleWrapper(Parcel in) {
        in.writeInt(userCreditStyle.getCreditState().ordinal());
        in.writeInt(userCreditStyle.getCreditOrder().ordinal());
        in.writeInt(userCreditStyle.getCreditFilter().ordinal());
        in.writeInt(userCreditStyle.getCreditFormat().ordinal());
        in.writeBoolean(userCreditStyle.isIgnore());
        in.writeBoolean(userCreditStyle.isReview());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        userCreditStyle = new UserCreditStyle();
        userCreditStyle.setCreditState(CreditState.values()[dest.readInt()]);
        userCreditStyle.setCreditOrder(CreditOrder.values()[dest.readInt()]);
        userCreditStyle.setCreditFilter(CreditFilter.values()[dest.readInt()]);
        userCreditStyle.setCreditFormat(CreditFormat.values()[dest.readInt()]);
        userCreditStyle.setIgnore(dest.readBoolean());
        userCreditStyle.setReview(dest.readBoolean());
    }

    public static final Creator<UserCreditStyleWrapper> CREATOR = new Creator<UserCreditStyleWrapper>() {
        @Override
        public UserCreditStyleWrapper createFromParcel(Parcel in) {
            return new UserCreditStyleWrapper(in);
        }

        @Override
        public UserCreditStyleWrapper[] newArray(int size) {
            return new UserCreditStyleWrapper[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public UserCreditStyle getUserCreditStyle() {
        return userCreditStyle;
    }

}
