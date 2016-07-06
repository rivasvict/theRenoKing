package com.rifluxyss.therenoking.tasks;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;
import android.content.Intent;

import com.rifluxyss.therenoking.ListProspects;

public class HandleRenoExceptions implements
		java.lang.Thread.UncaughtExceptionHandler {
	private final Context myContext;

	public HandleRenoExceptions(Context context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		final StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		System.err.println(stackTrace);

		 Intent intent = new Intent(myContext, ListProspects.class);
//		 intent.putExtra("praki", stackTrace.toString());
		 myContext.startActivity(intent);
		
//		try {
//			new AlertDialog.Builder(myContext)
//			.setPositiveButton("OK",
//					new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog,
//						int whichButton) {
//					Intent send = new Intent(Intent.ACTION_SENDTO);
//					String uriText;
//					uriText = "mailto:s.syedfarakatullah@rifluxyss.com"
//							+ "?subject=RenoKing Crash Report" + "&body=" + stackTrace.toString();
//					uriText = uriText.replace(" ", "%20");
//					Uri uri = Uri.parse(uriText);
//
//					send.setData(uri);
//					myContext.startActivity(Intent.createChooser(send, "Send mail..."));
//				}
//			})
//			.setNegativeButton("No",
//					new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog,
//						int whichButton) {
//					dialog.cancel();
//				}
//			}).setMessage("Sorry, There is a problem with the application. " +
//					"Please help us to review it. " +
//					"Do you want to send the crash report?").create().show();
//			return;
//		} catch (Exception e) {
//			Log.e("Alert error", "Alert Err: " + e.toString());
//		}

		System.exit(10);
	}
}
