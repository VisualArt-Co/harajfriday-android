package com.benAbdelWahed.responses.settings_response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class SettingsData implements Serializable {

    @SerializedName("conditions_participation_in_premium_membership")
    private String conditionsParticipationInPremiumMembership;

    @SerializedName("about_app")
    private String aboutApp;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("other")
    private String other;

    @SerializedName("id")
    private int id;

    @SerializedName("site_commission_account")
    private String siteCommissionAccount;

    @SerializedName("messageToNormalCustomer")
    private String messageToNormalCustomer;

    @SerializedName("messageToUpgradeToPremuim")
    private String messageToUpgradeToPremuim;

    @SerializedName("blackList")
    private String blackList;

    @SerializedName("serviceUseTreaty")
    private String serviceUseTreaty;

    @SerializedName("forbiddenProducts")
    private String forbiddenProducts;

    @SerializedName("discount_system")
    private String discountSystem;

    @SerializedName("contactUs")
    private String contactUs;

    @SerializedName("payment_options")
    private String paymentOptions;

    @SerializedName("yearly_subscrption_price")
    private String yearlySubscrptionPrice;

    @SerializedName("subscrption_in_premium_is_active")
    private String subscrptionInPremiumIsActive;

    @SerializedName("subscrption_in_premium_desactivation_message")
    private String subscrptionInPremiumDesactivationMessage;

    @SerializedName("number_of_ads_per_hour")
    private String numberOfAdsPerHour;

    public String getMessageToUpgradeToPremuim() {
        return messageToUpgradeToPremuim;
    }

    public void setMessageToUpgradeToPremuim(String messageToUpgradeToPremuim) {
        this.messageToUpgradeToPremuim = messageToUpgradeToPremuim;
    }

    public String getContactUs() {
        return contactUs;
    }

    public void setContactUs(String contactUs) {
        this.contactUs = contactUs;
    }

    public String getOther() {
        return other;
    }

    public String getConditionsParticipationInPremiumMembership() {
        return conditionsParticipationInPremiumMembership;
    }

    public void setConditionsParticipationInPremiumMembership(String conditionsParticipationInPremiumMembership) {
        this.conditionsParticipationInPremiumMembership = conditionsParticipationInPremiumMembership;
    }

    public String getAboutApp() {
        return aboutApp;
    }

    public void setAboutApp(String aboutApp) {
        this.aboutApp = aboutApp;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSiteCommissionAccount() {
        return siteCommissionAccount;
    }

    public void setSiteCommissionAccount(String siteCommissionAccount) {
        this.siteCommissionAccount = siteCommissionAccount;
    }

    public String getMessageToNormalCustomer() {
        return messageToNormalCustomer;
    }

    public void setMessageToNormalCustomer(String messageToNormalCustomer) {
        this.messageToNormalCustomer = messageToNormalCustomer;
    }

    public String getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }

    public String getServiceUseTreaty() {
        return serviceUseTreaty;
    }

    public void setServiceUseTreaty(String serviceUseTreaty) {
        this.serviceUseTreaty = serviceUseTreaty;
    }

    public String getForbiddenProducts() {
        return forbiddenProducts;
    }

    public void setForbiddenProducts(String forbiddenProducts) {
        this.forbiddenProducts = forbiddenProducts;
    }

    public String getDiscountSystem() {
        return discountSystem;
    }

    public void setDiscountSystem(String discountSystem) {
        this.discountSystem = discountSystem;
    }

    public String getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(String paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public String getYearlySubscrptionPrice() {
        return yearlySubscrptionPrice;
    }

    public void setYearlySubscrptionPrice(String yearlySubscrptionPrice) {
        this.yearlySubscrptionPrice = yearlySubscrptionPrice;
    }

    public String getSubscrptionInPremiumIsActive() {
        return subscrptionInPremiumIsActive;
    }

    public void setSubscrptionInPremiumIsActive(String subscrptionInPremiumIsActive) {
        this.subscrptionInPremiumIsActive = subscrptionInPremiumIsActive;
    }

    public String getSubscrptionInPremiumDesactivationMessage() {
        return subscrptionInPremiumDesactivationMessage;
    }

    public void setSubscrptionInPremiumDesactivationMessage(String subscrptionInPremiumDesactivationMessage) {
        this.subscrptionInPremiumDesactivationMessage = subscrptionInPremiumDesactivationMessage;
    }

    public String getNumberOfAdsPerHour() {
        return numberOfAdsPerHour;
    }

    public void setNumberOfAdsPerHour(String numberOfAdsPerHour) {
        this.numberOfAdsPerHour = numberOfAdsPerHour;
    }
}