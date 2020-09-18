package edu.sdsmt.calenderapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private String username;
    private String date;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Calendar calendar;
    private View popupEditDialogView = null;
    private AlertDialog alertDialog;

    private EditText txt_time;
    private EditText txt_desc;
    private int pos;
    private boolean edit;

    private String[] items;

    private class ListEvents extends AsyncTask<String, String, String> {

        private Context context;
        private AlertDialog alertDialog;

        private String type;


        ListEvents(Context ctx){
            context = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {

            String login_url;

            type = strings[0];
            if(type.equals("list")) {
                //Replace 192.168.2.9 with local ip address or 10.0.2.2
                login_url = "http://10.0.2.2/get_events.php";
                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                            + "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (type.equals("delete"))
            {
                try{
                    //Replace 192.168.2.9 with local ip address or 10.0.2.2
                    login_url = "http://10.0.2.2/codeigniter/index.php/api_controller/deleteevent";
                    String time = items[0];
                    String event = items[2];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream =  httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                            + "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")
                            + "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8")
                            + "&" + URLEncoder.encode("event", "UTF-8") + "=" + URLEncoder.encode(event, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while((line = bufferedReader.readLine())!= null){
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {

            if(type.equals("list")) {
                try {
                    adapter.clear();
                    JSONObject jsonResult = new JSONObject(result);
                    int success = jsonResult.getInt("success");
                    if (success == 1) {
                        JSONArray events = jsonResult.getJSONArray("events");
                        for (int i = 0; i < events.length(); i++) {
                            JSONObject event = events.getJSONObject(i);
                            String time = event.getString("time");
                            String desc = event.getString("event");
                            String line = time + " - " + desc;
                            adapter.add(line);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if (type.equals("delete")){
                if(result.equals("Event deleted successfully"))
                {
                    adapter.remove(adapter.getItem(pos));
                    adapter.notifyDataSetChanged();
                }
                if(!edit)
                    Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        edit = false;
        listView = findViewById((R.id.scroll_view));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        date = sdf.format(today);

        ListEvents le = new ListEvents(this);
        le.execute("list");

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) adapter.getItem(position);
                items = item.split(" ", 3);

                pos = position;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                alertDialogBuilder.setTitle("Edit Event");

                // Init popup dialog view and it's ui controls.
                initPopupViewControls();

                // Set the inflated layout view object to the AlertDialog builder.
                alertDialogBuilder.setView(popupEditDialogView);

                // Create AlertDialog and show.
                alertDialog = alertDialogBuilder.create();
                txt_desc.setText(items[2]);
                txt_time.setText(items[0]);
                alertDialog.show();

            }

        });
    }

    public void launchCalender(View view){
        Intent newIntent = new Intent(getApplicationContext(), CalendarActivity.class);
        newIntent.putExtra("username", username);
        startActivityForResult(newIntent, 1);
    }



    public void launchNotifications(View view){
        Intent newIntent = new Intent(getApplicationContext(), NotificationsActivity.class);
        newIntent.putExtra("username", username);
        startActivity(newIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("here");
        ListEvents le = new ListEvents(this);
        le.execute("list");
    }

    private void initPopupViewControls() {
        // Get layout inflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);

        // Inflate the popup dialog from a layout xml file.
        popupEditDialogView = layoutInflater.inflate(R.layout.edit_evnt_layout, null);


        txt_time = (EditText) popupEditDialogView.findViewById(R.id.txt_time_evnt);
        txt_desc = (EditText) popupEditDialogView.findViewById(R.id.txt_desc_evnt);
    }

    public void onDelete(View view){
        edit = false;
        ListEvents le1 = new ListEvents(HomeActivity.this);
        le1.execute("delete");
        alertDialog.hide();
    }

    public void onBtnCancel(View view){
        alertDialog.hide();
    }

    public void onEdit(View view){
        edit = true;
        ListEvents le1 = new ListEvents(HomeActivity.this);
        le1.execute("delete");

        String time = txt_time.getText().toString();
        String event = txt_desc.getText().toString();
        String type = "event";

        if(time.equals("") || event.equals("") ){
            AlertDialog alertDialog;
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("All the fields should be filled");
            alertDialog.show();

        }else {
            alertDialog.hide();
            BackgroundWorker bgworker = new BackgroundWorker(HomeActivity.this);
            bgworker.execute(type, username, date, time, event);
            ListEvents le2 = new ListEvents(HomeActivity.this);
            le2.execute("list");
        }
    }

    public void onBack(View view)
    {
        finish();
    }
}
