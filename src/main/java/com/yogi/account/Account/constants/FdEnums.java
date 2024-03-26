package com.yogi.account.Account.constants;

public enum FdEnums {
    id("id"), type("type"), number("number"), amount("amount"), accountOpenDate("accountOpenDate"), Roi("Roi"), maturityDate("maturityDate");


    private final String text;
    FdEnums(final String index) {
        this.text = index;
    }

    @Override
    public String toString() {
        return text;
    }
}
