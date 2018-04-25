package gvideo.sgutierc.cl.util;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import lib.file.metadata.FileReader;


public class Miscelaneous {
    public static String print(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (byte b : bytes) {
            sb.append(String.format("0x%02X ", b));
        }
        sb.append("]");
        return sb.toString();
    }

    private static boolean isVersionMajor() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean writeMetadata(Activity context, File file, String metadataName, byte[] data) {
        boolean result = false;
        if (isVersionMajor())
            try {
                UserDefinedFileAttributeView view = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
                ByteBuffer writeBuffer = ByteBuffer.allocate(data.length);
                writeBuffer.put(data);
                writeBuffer.flip();
                view.write(metadataName, writeBuffer);
                result = true;
            } catch (Exception e) {
                Log.e(Miscelaneous.class.getName(), e.toString());
                result = false;
            }
        else {
            FileReader metadataHelper = new FileReader();
                try {
                metadataHelper.writeXDataTo(file, metadataName, metadataHelper.toHex(data));
                result = true;
            } catch (Exception e) {
                Log.e(Miscelaneous.class.getName(), e.toString());
                result = false;
            }
        }
        return result;
    }

}
