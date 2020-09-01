package org.andresoviedo.app.model3D;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import org.andresoviedo.app.model3D.view.MenuActivity;
import org.andresoviedo.app.model3D.view.ModelActivity;
import org.andresoviedo.dddmodel2.R;
import org.andresoviedo.util.android.AndroidURLStreamHandlerFactory;
import org.andresoviedo.util.android.AssetUtils;
import org.andresoviedo.util.android.ContentUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the main android activity. From here we launch the whole stuff.
 * 
 * Basically, this activity may serve to show a Splash screen and copy the assets (obj models) from the jar to external
 * directory.
 * 
 * @author andresoviedo
 *
 */
public class MainActivity extends Activity {

	private Map<String, Object> loadModelParameters = new HashMap<>();


    // Custom handler: org/andresoviedo/util/android/assets/Handler.class
    static {
        System.setProperty("java.protocol.handler.pkgs", "org.andresoviedo.util.android");
        URL.setURLStreamHandlerFactory(new AndroidURLStreamHandlerFactory());
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



			ContentUtils.provideAssets(this);
			launchModelRendererActivity(Uri.parse("assets://" + getPackageName() + "/" + "models/model2.obj"));

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	/*private void loadModel() {
		ContentUtils.showListDialog(this, "File Provider", new String[]{"Samples", "Repository",
				"File Explorer", "Android Explorer"}, (DialogInterface dialog, int which) -> {
			loadModelFromAssets();
			});

	}*/

	private void loadModelFromAssets() {
		AssetUtils.createChooserDialog(this, "Select file", null, "models", "(?i).*\\.(obj|stl|dae)",
				(String file) -> {
					if (file != null) {
						ContentUtils.provideAssets(this);
						launchModelRendererActivity(Uri.parse("assets://" + getPackageName() + "/" + file));
					}
				});
	}

	private void launchModelRendererActivity(Uri uri) {
		Log.i("Menu", "Launching renderer for '" + uri + "'");
		Intent intent = new Intent(getApplicationContext(), ModelActivity.class);
		try {
			URI.create(uri.toString());
			intent.putExtra("uri", uri.toString());
		} catch (Exception e) {
			// info: filesystem url may contain spaces, therefore we re-encode URI
			try {
				intent.putExtra("uri", new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), uri.getFragment()).toString());
			} catch (URISyntaxException ex) {
				Toast.makeText(this, "Error: " + uri.toString(), Toast.LENGTH_LONG).show();
				return;
			}
		}
		intent.putExtra("immersiveMode", "false");

		// content provider case
		if (!loadModelParameters.isEmpty()) {
			intent.putExtra("type", loadModelParameters.get("type").toString());
			//intent.putExtra("backgroundColor", "0.25 0.25 0.25 1");
			loadModelParameters.clear();
		}

		startActivity(intent);
	}

}
