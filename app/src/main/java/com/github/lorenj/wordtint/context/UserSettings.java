package com.github.lorenj.wordtint.context;


public class UserSettings {

    /**
     * 是否已经同意用户协议
     */
    private boolean acceptUserAgreement;

    public boolean isAcceptUserAgreement() {
        return acceptUserAgreement;
    }

    public void setAcceptUserAgreement(boolean acceptUserAgreement) {
        this.acceptUserAgreement = acceptUserAgreement;
    }

}
