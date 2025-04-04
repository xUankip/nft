package com.nftgallery.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {

    // Xác thực tên NFT
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() > 5;
    }

    // Xác thực giá
    public static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    // Xác thực người tạo
    public static boolean isValidCreator(String creator) {
        return creator != null && !creator.trim().isEmpty();
    }

    // Xác thực URL hình ảnh
    public static boolean isValidImageUrl(String url) {
        String urlRegex = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
        return url != null && Pattern.matches(urlRegex, url);
    }

    // Xác thực địa chỉ ví Ethereum
    public static boolean isValidEthAddress(String address) {
        String ethAddressRegex = "^0x[a-fA-F0-9]{40}$";
        return address != null && Pattern.matches(ethAddressRegex, address);
    }
}