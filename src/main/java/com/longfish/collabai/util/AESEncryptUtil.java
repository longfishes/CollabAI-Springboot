package com.longfish.collabai.util;

import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.properties.ProjectProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AESEncryptUtil {

    private static final String ALGORITHM = "AES";

    @Autowired
    private ProjectProperties projectProperties;

    public String encrypt(String data) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(projectProperties.getAesSecretKey().getBytes(), ALGORITHM);
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedBytes = cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedData) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(projectProperties.getAesSecretKey().getBytes(), ALGORITHM);
        byte[] decryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        } catch (Exception e) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }
        return new String(decryptedBytes);
    }
}
