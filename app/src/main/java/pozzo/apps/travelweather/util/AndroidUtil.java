package pozzo.apps.travelweather.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ScrollView;

import java.util.List;
import java.util.Locale;

/**
 * Utility tools related to Android.
 *
 * @author sarge
 * @since 22/07/15.
 */
public class AndroidUtil {
    /**
     * Hide keyaboard.
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Show keyboard.
     */
    public static void showKeyboard(Context context, View view) {
        if(view.requestFocus()) {
            InputMethodManager keyboard = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(view, 0);
        }
    }

    /**
     * Restart application.
     */
    public static void restartApplication(Context context) {
        Intent startMain = context.getPackageManager()
                .getLaunchIntentForPackage(context.getApplicationContext().getPackageName());
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(startMain);
    }

    /**
     * Redirect to any link.
	 *
	 * @return true if succed.
     */
    public static boolean openUrl(String link, Context context) {
        if(context == null)
            return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

		PackageManager manager = context.getPackageManager();
		List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
		if (infos.size() > 0) {
			context.startActivity(intent);
			return true;
		}
		return false;
    }

    /**
     * @return true if you are connecting on a mobile network.
     */
    public static boolean isMobileNetwork(Context context) {
        if(context == null)
            return false;

        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    }

    /**
     * @return true se aparentemete houver conexao.
     */
    public static boolean isNetworkAvailable(Context context) {
        if(context == null)
            return false;

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Scroll to given position smoothly.
     */
    public static void scrollTo(final ScrollView scrollView, final int bottom) {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, bottom);
            }
        }, 400);
    }

    /**
     * Calls any compatible application to open the given file url (web or local).
     *
     * @return true if there is an app to open this path.
     */
    public static boolean viewFile(String path, Context context) {
        String mimeType = getMimeType(path);

        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * @return Extension from the given file.
     */
    public static String getFileExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }

    /**
     * @return MimeType from given path.
     */
    public static String getMimeType(String filePath) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                getFileExtension(filePath).toLowerCase(Locale.US));
    }

    /**
     * Exibe uma mensagem de erro.
     */
    public static AlertDialog.Builder errorMessage(
            Context context, String errorMessage, int errorTitle, int okButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(errorTitle);
        builder.setMessage(errorMessage);
        builder.setPositiveButton(okButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder;
    }
}
