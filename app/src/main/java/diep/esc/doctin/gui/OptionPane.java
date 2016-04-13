package diep.esc.doctin.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Diep on 04/04/2016.
 * This class provide a simple static method to open a dialog.
 *
 * @author Diep
 */
public class OptionPane {

    /**
     * Show simple dialog with a message
     *
     * @param context current context
     * @param title   dialog title
     * @param message message to be showed on dialog
     */
    public static void showMessageDialog(Context context, String title, String message) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setTitle(title);
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
}
