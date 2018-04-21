package gauravtak.gpstrackingpoc.utils_classes;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;


import gauravtak.gpstrackingpoc.R;

public class AlertDialogUtil
{
    public static void showErrorDialog(final Activity activity, String msg) {
        if (activity.isFinishing())
            return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(msg).setTitle(activity.getString(R.string.error)).setCancelable(false).setPositiveButton(activity.getString(R.string.okay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public static void showAlertDialogWithFinishActivity( final Activity activity,String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton(activity.getString(R.string.okay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                activity.finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}

