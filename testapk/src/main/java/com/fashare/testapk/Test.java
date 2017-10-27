package com.fashare.testapk;

import android.content.Context;
import android.widget.Toast;

public class Test {
    public static void sayHello(Context context){
        Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
    }
}
