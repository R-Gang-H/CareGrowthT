package com.caregrowtht.app.user;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by haoruigang on 2018-4-26 18:39:50.
 */

public class MultipleItem implements MultiItemEntity {

    public static final int TYPE_NO_SIGN_IN = 0;
    public static final int TYPE_SIGN_IN = 1;
    public static final int TYPE_LEAVE = 2;
    public static final int TYPE_ORIGIN = 3;
    public static final int TYPE_STATUS = 4;
    public static final int TYPE_RECEIPT_NO = 5;
    public static final int TYPE_RECEIPT_ALL = 6;

    private int itemType;

    public MultipleItem(int itemType) {
        this.itemType = itemType;
    }


    @Override
    public int getItemType() {
        return itemType;
    }
}
