package com.example.sociar;

import android.content.Context;

import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.SessionResponse;

import java.util.Date;

public class FAQ {

    // declarations
    private Assistant watsonAssistant;
    private Response<SessionResponse> watsonAssistantSession;
    Context context;

    public FAQ(Context context){
        // context should be HelpActivity.java
        this.context = context; // pass in an activity context so getSystemService in checkInternetConnections() works
        // create a watson service at the beginning
        watsonAssistant = new Assistant("2019-02-28", new IamAuthenticator(context.getString(R.string.chatbot_apikey)));
        watsonAssistant.setServiceUrl(context.getString(R.string.chatbot_url));
    }

    public String search(String question){
        class SearchRunnable implements Runnable{
            private String ans;

            public void run() {
                try {
                    if (watsonAssistantSession == null) {
                        ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(context.getString(R.string.chatbot_id)).build());
                        watsonAssistantSession = call.execute();
                    }

                    MessageInput input = new MessageInput.Builder()
                            .text(question)
                            .build();
                    MessageOptions options = new MessageOptions.Builder()
                            .assistantId(context.getString(R.string.chatbot_id))
                            .input(input)
                            .sessionId(watsonAssistantSession.getResult().getSessionId())
                            .build();

                    Response<MessageResponse> response = watsonAssistant.message(options).execute();
                    ans = response.getResult().getOutput().getGeneric().get(0).text(); // answer received from Watson

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            public String getResponse(){
                return ans;
            }
        }
        SearchRunnable searchRunnable = new SearchRunnable();
        Thread thread = new Thread(searchRunnable);
        thread.start();

        // error handling: if wait time > 1s, abandon search
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        while (elapsedTime < 1000) {
            //perform db poll/check
            elapsedTime = (new Date()).getTime() - startTime;
        }

//        Log.i(TAG, "running timer");
        try {
            thread.join();
            return searchRunnable.getResponse();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Your assistant is taking too long to respond, please check your Internet.";
        }

    }
}

