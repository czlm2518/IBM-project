package com.example.sociar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class HelpActivity extends AppCompatActivity {

    Button button_send_query;
    EditText et_query;
    RecyclerView recyclerView;

    ArrayList<String> list_messages = new ArrayList<String>();
    String mes;

    FAQ faq;


    //inflate page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        init();
    }

    private void init(){
        recyclerView = findViewById(R.id.rv_messages);
        // attach the recycler view to a LINEAR layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        faq = new FAQ(this);

        list_messages.add("Hi. Please send 'FAQ' to see what you can ask.");
        // the recycler view adapter
        HelpMessageAdapter helpMessageAdapter = new HelpMessageAdapter(this, list_messages);
        recyclerView.setAdapter(helpMessageAdapter);
        button_send_query = findViewById(R.id.button_send_query);
        et_query = findViewById(R.id.et_query);

        button_send_query.setOnClickListener(
                //  takes user input after clicking on send
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mes = et_query.getText().toString();
                        et_query.setText("");
                        if (mes !="" && checkInternetConnection()){
                            list_messages.add(mes);
                            helpMessageAdapter.notifyItemInserted(list_messages.size()-1);
                            list_messages.add(faq.search(mes));
                            helpMessageAdapter.notifyItemInserted(list_messages.size()-1);
                        }
                    }});
    };

    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }
}
