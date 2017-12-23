package me.anouar.paintme;

import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by anouarkappitou on 10/29/17.
 */

public class SignatorExporter {


    private String _file_path;

    private Bitmap _bitmap;

    private int DEFAULT_WIDTH = 250;
    private int DEFAULT_HEIGHT = 250;


    public SignatorExporter( Bitmap bitmap , String file_path )
    {
        _file_path = file_path;
        _bitmap = bitmap;
    }


    private void add_extention( String extention )
    {
        if( !_file_path.contains("." + extention ) )
            _file_path += "." + extention;
    }

    public void save_as_png() throws IOException {
        add_extention("png");
        save( Bitmap.CompressFormat.PNG , DEFAULT_WIDTH , DEFAULT_HEIGHT );
    }

    public void save_as_jpeg() throws IOException {
        add_extention("jpeg");
        save( Bitmap.CompressFormat.JPEG, DEFAULT_WIDTH , DEFAULT_HEIGHT );
    }

    public void save(Bitmap.CompressFormat format, int width, int height) throws IOException {
        FileOutputStream stream = new FileOutputStream( _file_path );
        Bitmap final_bitmap = Bitmap.createScaledBitmap(_bitmap, width , height , true );
        final_bitmap.compress( format , 100 , stream );
        stream.close();
    }
}
