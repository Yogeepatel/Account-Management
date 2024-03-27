package com.yogi.account.Account.constants;

public enum FdEnums {
    id("id"), type("type"), number("number"), amount("amount"), accountOpenDate("accountOpenDate"), Roi("accountOpenDate"), bankName("accountOpenDate"), maturityDate("accountOpenDate");


    private final String text;
    FdEnums(final String index) {
        this.text = index;
    }

    @Override
    public String toString() {
        return text;
    }
}
