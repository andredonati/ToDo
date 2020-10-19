package edu.csce4623.ahnelson.todomvp3.addedittodoitem;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import edu.csce4623.ahnelson.todomvp3.R;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;
import edu.csce4623.ahnelson.todomvp3.todolistactivity.ToDoListActivity;
import edu.csce4623.ahnelson.todomvp3.todolistactivity.ToDoListContract;
import edu.csce4623.ahnelson.todomvp3.todolistactivity.ToDoListFragment;
import edu.csce4623.ahnelson.todomvp3.todolistactivity.ToDoListPresenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEditToDoItemActivity extends AppCompatActivity {
    public String title;
    public String content;
    EditText etTitle;
    EditText etContent;
    EditText etDateTime;
    private ToDoListPresenter mPresenter;
    public Integer id;
    boolean checked = false;

    private static Calendar dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_to_do_item);
        etTitle = (EditText) findViewById(R.id.etItemTitle);
        etContent = (EditText) findViewById(R.id.etItemContent);
        etDateTime = (EditText) findViewById(R.id.tvDateTime);
        dateTime = Calendar.getInstance();


        //when opening a to do item
        final Intent intent = getIntent();
        if(mPresenter.UPDATE_TODO_REQUEST == 1){
            title = intent.getStringExtra("Title");
            content = intent.getStringExtra("Content");
            etTitle.setText(title);
            etContent.setText(content);
            //fix this brother
            long due = intent.getLongExtra("DueDate", 0);
            String dueDate = DateFormat.format("dd-MM-yyyy HH:mm", new Date(due)).toString();
            etDateTime.setText(dueDate);
            id = intent.getIntExtra("ID", -1);
            checked = intent.getBooleanExtra("Completed", false);


            CheckBox checkbox = findViewById(R.id.done);
            if(checked){
                checkbox.setChecked(true);
            }
            else{
                checkbox.setChecked(false);
                checked = false;
            }
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        checked = true;
                    }else{
                        checked = false;
                    }
                }
            });
        }

        //code for date and alarm

        etDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
                etDateTime.setText(DateFormat.format("dd-MM-yyyy HH:mm", dateTime.getTimeInMillis()));

            }
        });

    }

    public void onClick(View view) {
        try{
            //getting values and setting them to the string values
            title = etTitle.getText().toString();
            content = etContent.getText().toString();
            //creating a to do item and setting the title and content to it
            ToDoItem toDoItem = new ToDoItem();
            toDoItem.setTitle(title);
            toDoItem.setContent(content);
            toDoItem.setDueDate(dateTime.getTimeInMillis());
            toDoItem.setCompleted(checked);
            if(checked){
                toDoItem.setTitle(title+ " [COMPLETED]");
            }
            else{
//                toDoItem.setTitle(title.substring(0,title.length() - 12));
            }
            //get intent and put new to do data in intent
            toDoItem.setId(Integer.valueOf(id));
            Intent intent = getIntent();
            intent.putExtra("ToDoItem", toDoItem);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void showDatePickerDialog(View view) {
        DialogFragment dateFrag = new DatePickerFragment();
        dateFrag.show(getSupportFragmentManager(), "datePicker");
        AlarmManager alarmManager;
        if(Build.VERSION.SDK_INT >= 23){
            alarmManager = view.getContext().getSystemService(AlarmManager.class);

        }else{
            alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
        }
        dateTime.setTimeInMillis(System.currentTimeMillis());
        dateTime.set(Calendar.SECOND, Calendar.DAY_OF_MONTH,Calendar.DATE,Calendar.HOUR_OF_DAY, Calendar.MINUTE);
        Intent alarmNotificationIntent = new Intent(view.getContext(), AlarmNotificationReciever.class);
        alarmNotificationIntent.putExtra("Title", title);
        alarmNotificationIntent.putExtra("Content", content);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(view.getContext(), 0, alarmNotificationIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(),alarmIntent);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public DatePickerDialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            dateTime.set(year,month,day);
            showTimePickerDialog(view, getActivity().getSupportFragmentManager());
        }
        private void showTimePickerDialog(View view, FragmentManager fragmentManager) {
            DialogFragment timeFrag = new TimePickerFragment();
            timeFrag.show(fragmentManager, "timePicker");
        }

        public static class TimePickerFragment extends DialogFragment
                implements TimePickerDialog.OnTimeSetListener {

            @Override
            public TimePickerDialog onCreateDialog(Bundle savedInstanceState){
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                return new TimePickerDialog(getActivity(),this, hour, minute, DateFormat.is24HourFormat(getActivity()));

            }
            public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                dateTime.add(Calendar.HOUR, hourOfDay);
                dateTime.add(Calendar.MINUTE, minute);
            }


        }
    }
}
