package scripts.magic;

import org.tribot.api.General;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Nolan on 10/16/2015.
 */
public class DynamicSignature { //wtf

    public static boolean sendSignatureData(long runtimeInSeconds, int xp, int var2, int var3, int var4) {

        // In order to provide some security, so that people will not tamper the data and post it themselves, we will be encrypting it here and decrypting it in php.
        // These keys should be the same as in PHP (db.php) (16bit long)
        String privateKey = "8E5181A386A6937F";
        String initVector = "83D7BD63E711EFD4";

        try {
            // data we will be encrypting. you can remove the var's if you want (username and runtime are required though)
            String data = initVector + "," + General.getTRiBotUsername() + "," + runtimeInSeconds + "," + xp + "," + var2 + "," + var3 + "," + var4; // comma delimited so we can split in php

            // set up iv and key for encrypting
            IvParameterSpec ivspec = new IvParameterSpec(initVector.getBytes());
            SecretKeySpec keyspec = new SecretKeySpec(privateKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));

            // convert to hex
            String token = "";
            for (int i = 0; i < encrypted.length; i++) { //what is this
                if ((encrypted[i] & 0xFF) < 16) {
                    token = token + "0" + java.lang.Integer.toHexString(encrypted[i] & 0xFF);
                } else {
                    token = token + java.lang.Integer.toHexString(encrypted[i] & 0xFF);
                }
            }

            // And post it :)
            URL url = new URL("http://www.sigmascripts.freeiz.com/input.php?token=" + token);
            URLConnection conn = url.openConnection();

            // fake request coming from browser (solves permission issue on shared webhosting)
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            in.readLine();
            in.close();


            return true;
        } catch (Exception e) {
            General.println(e.getMessage());
        }
        return false;
    }
}
