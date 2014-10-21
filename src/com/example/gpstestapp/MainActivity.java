package com.example.gpstestapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	static final int READ_BLOCK_SIZE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class PlaceholderFragment extends Fragment implements
			LocationListener {

		protected EditText editTextNombre;
		protected EditText editTextLugar;

		protected Button mButton;
		protected Button nButton;
		protected Button lButton;
		protected Button oButton;
		protected EditText editTextAltitud;
		protected EditText editTextLongitud;
		protected EditText editTextLatitud;
		protected boolean mStarted;
		private LocationManager mLocationManager;
		private PlaceholderFragment listener = this;
		public BufferedWriter buffer;
		private String TAG = PlaceholderFragment.class.getSimpleName();

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_main,
					container, false);

			mStarted = false;

			mButton = (Button) rootView.findViewById(R.id.button1);
			nButton = (Button) rootView.findViewById(R.id.button2);
			lButton = (Button) rootView.findViewById(R.id.button3);
			oButton = (Button) rootView.findViewById(R.id.button4);
			editTextNombre = (EditText) rootView
					.findViewById(R.id.editTextnombre);
			editTextLugar = (EditText) rootView
					.findViewById(R.id.editTextLugar);
			editTextAltitud = (EditText) rootView
					.findViewById(R.id.editTextAltitud);
			editTextLongitud = (EditText) rootView
					.findViewById(R.id.editTextLongitud);
			editTextLatitud = (EditText) rootView
					.findViewById(R.id.editTextLatitud);

			mButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mStarted == false) {
						starCapturing();
						Toast.makeText(getBaseContext(), "Iniciado",
								Toast.LENGTH_SHORT).show();
					}

				}
			});

			nButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mStarted == true) {
						stopCapturing();
						mStarted = !mStarted;
						Toast.makeText(getBaseContext(), "Detenido",
								Toast.LENGTH_SHORT).show();

					}
//					editTextLugar.setText("");
//					editTextLongitud.setText("");
//					editTextAltitud.setText("");
//					editTextLatitud.setText("");

				}

			});

			lButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						FileInputStream fis = openFileInput("textFile.txt");
						InputStreamReader isr = new InputStreamReader(fis);

						char[] inputBuffer = new char[READ_BLOCK_SIZE];
						String s = "";

						int charRead;
						while ((charRead = isr.read(inputBuffer)) > 0) {
							// Convertimos los char a String
							String readString = String.copyValueOf(inputBuffer,
									0, charRead);
							s += readString;

							inputBuffer = new char[READ_BLOCK_SIZE];
						}
						
						try {
							File tarjeta = Environment
									.getExternalStorageDirectory();
							File file = new File(tarjeta.getAbsolutePath(),"Locacion.txt");
							OutputStreamWriter osw = new OutputStreamWriter(
									new FileOutputStream(file));
							osw.write(s+"\n");
							osw.flush();
							osw.close();

						} catch (IOException ioe) {
						}
						
						String[] to = { "Email@ejemplo.com" };
						String[] cc = { "Email@ejemplo.com" };
						enviar(to, cc, "Archivo Adjunto",
								"Te envio la informacion de las locaciones.");
						Toast.makeText(
								getBaseContext(),
								"Presione en el menu de arriba, presiona adjuntar imagen y busca el archivo localidad.txt",
								Toast.LENGTH_LONG).show();

						
						isr.close();
						
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					
					editTextLugar.setText("");
					editTextLongitud.setText("");
					editTextAltitud.setText("");
					editTextLatitud.setText("");
					
				}

			});
			
			

			oButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});

			return rootView;
		}
		
		private void enviar(String[] to, String[] cc,
				String asunto, String mensaje) {
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setData(Uri.parse("mailto:"));
				emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
				emailIntent.putExtra(Intent.EXTRA_CC, cc);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
				emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
				emailIntent.setType("message/rfc822");
				startActivity(Intent.createChooser(emailIntent, "Email "));
			}
		
		public void starCapturing() {
			boolean gps_enabled = true;
			boolean network_enabled = true;

			mLocationManager = (LocationManager) getActivity()
					.getSystemService(Context.LOCATION_SERVICE);

			try {
				gps_enabled = mLocationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
			} catch (Exception ex) {

			}
			try {
				network_enabled = mLocationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			} catch (Exception ex) {

			}
			Log.d(TAG, "gps_enabled " + gps_enabled + " network_enabled "
					+ network_enabled);
			if (!gps_enabled || !network_enabled) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				dialog.setMessage("GPS no esta habilitado!").setPositiveButton(
						"OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {

							}
						});
				dialog.show();

			} else {
				mLocationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, listener);
				mStarted = !mStarted;
			}
		}

		public void stopCapturing() {
			if (mLocationManager != null) {
				mLocationManager.removeUpdates(this);
			}
		}

		public void onStop() {
			super.onStop();
			Log.d(TAG, "onStop");
			stopCapturing();
		}

		@Override
		public void onResume() {
			super.onResume();
			Log.d(TAG, "onResume");
			if (mStarted) {
				starCapturing();
			} else {

			}
		}

		@Override
		public void onLocationChanged(Location location) {
			int i = 0;
			double mLat = location.getLatitude();
			double mLong = location.getLongitude();
			double mAlt = location.getAltitude();
			editTextLatitud.setText(String.valueOf(mLat));
			editTextLongitud.setText(String.valueOf(mLong));
			editTextAltitud.setText(String.valueOf(mAlt));

			
			String editTextNom = editTextNombre.getText().toString();
			String editTextLug = editTextLugar.getText().toString();

			try {
				FileOutputStream fos = openFileOutput("textFile.txt",
						MODE_APPEND);
				OutputStreamWriter osw = new OutputStreamWriter(fos);

				// Escribimos el String en el archivo

				osw.write(editTextNom + "," + editTextLug + ","
						+ editTextLongitud.getText() + ","
						+ editTextAltitud.getText() + ","
						+ editTextLatitud.getText() + "\n");

				osw.flush();
				osw.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

	}
}
