package me.anouar.paintme;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.IOException;

import me.anouar.filedialog.FileDialog;
import me.anouar.filedialog.SaveFileDialog;

public class MainActivity extends AppCompatActivity {


	private int PICK_FOLDER_RESULT_CODE = 10000;
	
	//custom drawing view
	private SignCanvas drawView;

	private Button _btn_clear , _btn_save;


	private boolean permission_granted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get drawing view
		drawView = (SignCanvas)findViewById(R.id.drawing);
        _btn_clear = (Button) findViewById( R.id.btn_clear );
		_btn_save = (Button) findViewById( R.id.btn_save );

		// Check if we're running on Android 5.0 or higher
		if (Build.VERSION.SDK_INT >= 23) {
			// Call some material design APIs here
			request_permission();
		} else {
			// Implement this feature without material design
            permission_granted = true;
		}

		_btn_clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				drawView.clear();
			}
		});

		_btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if( permission_granted )
				    save();
			}
		});

	}

	private void save()
	{
		FileDialog dialog = new SaveFileDialog();
		dialog.setStyle(DialogFragment.STYLE_NO_TITLE , R.style.Dialog );
        dialog.set_listener(new FileDialog.OnFileSelectedListener() {
			@Override
			public void onFileSelected(FileDialog dialog, File file) {
                SignatorExporter exporter = new SignatorExporter( drawView.get_bitmap() , file.getAbsolutePath() );
				try {
					exporter.save_as_png();
                    Toast.makeText(getApplicationContext(),R.string.saved , Toast.LENGTH_LONG ).show();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(),R.string.not_saved , Toast.LENGTH_LONG ).show();
				}
			}
		});
		dialog.show(getSupportFragmentManager(), SaveFileDialog.class.getName());
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == PICK_FOLDER_RESULT_CODE && resultCode == Activity.RESULT_OK) {

			String folderLocation = intent.getExtras().getString("data");
			Log.i( "folderLocation", folderLocation );

			SignatorExporter exporter = new SignatorExporter( drawView.get_bitmap() , folderLocation );
			try {
				exporter.save_as_png();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}


/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
		{
			case R.id.color_picker:
			{
                show_color_picker_dialog(new ColorPickedInterface() {
					@Override
					public void onColorPicked(String color) {
						drawView.setColor( color );
					}
				});
				return true;
			}

			case R.id.background_color:
			{

			}

		}

		return super.onOptionsItemSelected(item);
	}

	private interface ColorPickedInterface
	{
		void onColorPicked(String color);
	}

	private void show_color_picker_dialog(final ColorPickedInterface callback )

	{
		ColorPickerDialogBuilder
				.with(this)
				.setTitle("Choose color")
				.wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
				.density(12)
				.setOnColorSelectedListener(new OnColorSelectedListener() {
					@Override
					public void onColorSelected(int selectedColor) {
					}
				})
				.setPositiveButton("ok", new ColorPickerClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        callback.onColorPicked( "#" + Integer.toHexString( selectedColor ) );
					}
				})
				.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.build()
				.show();
	}





	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1: {

				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {

					// permission was granted, yay! Do the

					permission_granted = true;

					// contacts-related task you need to do.
				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}


	public void request_permission()
	{
        //android.permission.READ_EXTERNAL_STORAGE
		ActivityCompat.requestPermissions(MainActivity.this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE },
				1);
	}



}
