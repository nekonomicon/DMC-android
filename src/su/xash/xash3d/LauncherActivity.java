package su.xash.xash3d;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import java.io.File;

import su.xash.xash3d.dmc.R;

public class LauncherActivity extends Activity {
	static EditText cmdArgs;
	static SharedPreferences mPref;
	static Spinner modSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Build layout
		setContentView(R.layout.activity_launcher);	
		cmdArgs = (EditText)findViewById(R.id.cmdArgs);
		cmdArgs.setSingleLine(true);
		modSpinner= (Spinner)findViewById(R.id.modSpinner);
                modSpinner.setEnabled(true);
		final String[] list = {
                        "Deathmatch Classic",
                        "ThreeWave CTF"
		};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                modSpinner.setAdapter(adapter);
		mPref = getSharedPreferences("mod", 0);
		cmdArgs.setText(mPref.getString("argv", "-dev 3 -log"));
		modSpinner.setSelection(mPref.getInt("spinner", 0));
	}

	public void startXash(View view)
	{
		String gamedir;
		Intent intent = new Intent();
		intent.setAction("in.celest.xash3d.START");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		String argv = cmdArgs.getText().toString();

		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("argv", argv);
		editor.putInt("spinner", modSpinner.getSelectedItemPosition());
		editor.commit();

		if(modSpinner.getSelectedItemPosition() == 1)
		{
			gamedir = "3wave";
		}
		else
		{
			gamedir = "dmc";
		}

		if(cmdArgs.length() != 0) intent.putExtra("argv", argv);
		// Uncomment to set gamedir here
		intent.putExtra("gamedir", gamedir);
		intent.putExtra("gamelibdir", getFilesDir().getAbsolutePath().replace("/files", "/lib"));

		PackageManager pm = getPackageManager();
		ComponentName cn = intent.resolveActivity(pm);
		if(cn != null)
		{
			String packageName = cn.getPackageName();
			String versionName;
			try{
				versionName = pm.getPackageInfo(packageName, 0).versionName;
			}catch(PackageManager.NameNotFoundException e){
				showXashErrorDialog(e.toString());
				return;
			}

			if(0 <= versionName.compareTo("0.19.3"))
				startActivity(intent);
			else
				showXashUpdateDialog();
		}
		else
		{
			showXashInstallDialog("Xash3D FWGS ");
		}
	}

	public void showXashDialog(String title, String msg)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title)
		.setMessage(msg)
		.show();
	}

	public void showXashErrorDialog(String msg)
	{
		showXashDialog("Xash Error", msg);
	}

	public void showXashInstallDialog(String msg)
	{
		showXashErrorDialog(msg + getString(R.string.alert_install_dialog_text));
	}

	public void showXashUpdateDialog()
	{
		showXashErrorDialog(getString(R.string.alert_update_dialog_text));
	}
}
