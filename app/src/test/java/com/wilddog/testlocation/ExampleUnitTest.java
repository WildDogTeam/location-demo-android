package com.wilddog.testlocation;

import android.util.Log;

import com.wilddog.client.auth.CustomTokenGenerator;
import com.wilddog.client.auth.TokenOptions;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        String secret = "<your-secret>";
        TokenOptions options = new TokenOptions();
        options.setExpires(new Date(System.currentTimeMillis() + 2 * 24 * 3600 * 1000L));
        String token = CustomTokenGenerator.createAdminToken(secret, options);
        Log.e( "addition_isCorrect: ", token);

    }
}