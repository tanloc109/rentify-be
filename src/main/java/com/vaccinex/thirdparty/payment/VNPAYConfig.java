package com.vaccinex.thirdparty.payment;

import com.vaccinex.base.config.AppConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.*;

@Named
@ApplicationScoped
public class VNPAYConfig {

    @Getter
    private final String vnp_PayUrl;
    @Getter
    private final String vnp_ReturnUrl;
    @Getter
    private String vnp_TmnCode;
    @Getter
    private final String secretKey;
    private final String vnp_Version;
    private final String vnp_Command;
    private final String orderType;

    public VNPAYConfig() {
        this.vnp_PayUrl = AppConfig.getProperty("payment.vnPay.url");
        this.vnp_ReturnUrl = AppConfig.getProperty("payment.vnPay.returnUrl");
        this.vnp_TmnCode = AppConfig.getProperty("payment.vnPay.tmnCode");
        this.secretKey = AppConfig.getProperty("payment.vnPay.secretKey");
        this.vnp_Version = AppConfig.getProperty("payment.vnPay.version");
        this.vnp_Command = AppConfig.getProperty("payment.vnPay.command");
        this.orderType = AppConfig.getProperty("payment.vnPay.orderType");
    }

    public VNPAYConfig(String vnpTmnCode) {
        this(); // Call the default constructor to initialize fields
        // Override tmnCode if provided
        if (vnpTmnCode != null && !vnpTmnCode.isEmpty()) {
            this.vnp_TmnCode = vnpTmnCode;
        }
    }

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", this.vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef", VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }
}