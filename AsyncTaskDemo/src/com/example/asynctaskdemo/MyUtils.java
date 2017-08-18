package com.example.asynctaskdemo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class MyUtils {

	public static void close(BufferedInputStream in) {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void close(BufferedOutputStream out) {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
