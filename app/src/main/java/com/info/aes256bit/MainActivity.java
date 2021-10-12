package com.info.aes256bit;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    //static String plainText = "This is a plain text which need to be encrypted by Java AES 256 GCM Encryption Algorithm";
    String plainText;
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    private TextView textView, textView2, textView3;
    private Button button, button2;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        button2 = findViewById(R.id.button2);
        editText = findViewById(R.id.editTextTextPersonName);


        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");

            System.out.println("Original Text : " + plainText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey key = keyGenerator.generateKey();
        byte[] IV = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        keyGenerator.init(AES_KEY_SIZE);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plainText = editText.getText().toString();
                byte[] cipherText = new byte[0];
                try {
                    cipherText = encrypt(plainText.getBytes(), key, IV);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    System.out.println("Encrypted Text : " + Base64.getEncoder().encodeToString(cipherText));
                    textView.setText(Base64.getEncoder().encodeToString(cipherText));
                }

                String decryptedText = null;
                try {
                    decryptedText = decrypt(cipherText, key, IV);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("DeCrypted Text : " + decryptedText);
                textView3.setText(decryptedText);


            }
        });

    }


    public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] IV) throws Exception {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

        // Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);

        return cipherText;
    }

    public static String decrypt(byte[] cipherText, SecretKey key, byte[] IV) throws Exception {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

        // Perform Decryption
        byte[] decryptedText = cipher.doFinal(cipherText);

        return new String(decryptedText);
    }
}