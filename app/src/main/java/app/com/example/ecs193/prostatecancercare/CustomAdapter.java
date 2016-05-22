package app.com.example.ecs193.prostatecancercare;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by liray on 5/12/2016.
 */
public class CustomAdapter extends ArrayAdapter<String> {
    boolean[] responses;
    String[] dates;
    String[] treatments;

    public CustomAdapter(Context context, String[] treatments, boolean[] responses, String[] dates) {
        super(context, R.layout.treatment_entry, treatments);
        this.responses = responses;
        this.dates = dates;
        this.treatments = treatments;
    }

    public static class DatePickerFragment extends Fragment implements SimpleDatePickerDialog.OnDateSetListener {

        private Button button;
        @Override
        public void onDateSet(int year, int monthOfYear) {
            Bundle bundle = this.getArguments();
            String dates[] = null;
            int position = 0;
            if (bundle != null) {
                position = bundle.getInt("position", 0);
                dates = bundle.getStringArray("dates");
            }
            button.setText("" + (monthOfYear + 1) + "/" + year);
            dates[position] = "" + (monthOfYear + 1) + "/" + year;
        }

        public void setButton(Button button) {
            this.button = button;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View customView = inflater.inflate(R.layout.treatment_entry, parent, false);
        final TextView textView = (TextView)customView.findViewById(R.id.treatment_textview);
        if (position != 7 || textView.getText().toString().equals("Medium Text")) {
            textView.setText(getItem(position));
        }

        if (position == 7) {
            customView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Please enter the treatment");

                    // Set up the input
                    final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            treatments[position] = input.getText().toString();
                            textView.setText(input.getText().toString());
                        }
                    });
                    builder.show();
                }
            });
        }

        ((Switch)customView.findViewById(R.id.switch1)).setChecked(responses[position]);
        ((Switch)customView.findViewById(R.id.switch1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    responses[position] = true;
                } else {
                    responses[position] = false;
                }
            }
        });

        ((Button)customView.findViewById(R.id.button)).setText(dates[position]);
        ((Button)customView.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDatePickerDialogFragment datePickerDialogFragment;
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
                DatePickerFragment fragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putStringArray("dates", dates);
                fragment.setButton((Button)customView.findViewById(R.id.button));
                fragment.setArguments(bundle);
                datePickerDialogFragment.setOnDateSetListener(fragment);
                datePickerDialogFragment.show(((QolMedicalConditionActivity)getContext()).getSupportFragmentManager(), null);
            }
        });

        return customView;
    }
}
