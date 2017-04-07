package com.saltstudios.ftdiscovery.collaborativechatbot;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //teaching variables:
        final boolean[] learning = {false};
        final boolean[] initiative = {false};
        final String[] key = {""};

        //names
        final String user = "You";
        final String botName = "Codfish";
        final String[] personality = {"default"};

        //words that can't be taught. don't ever say these words
        final String[] doNotTeach = {"pussy", "nigger", "dick", "twat", "bitch", "penis", "vagina", "douche", "chink", "fuck", "frick", "frack", "fack", "nigga", "bish", "cunt", "asshole", "faggot", "autism"};
        final int[] swearCount = {0};

        //spelling mistakes of common words.
        final String[] what = {"wat", "waht", "whatt", "whut", "whaat"};
        final String[] you = {"u"};
        final String[] like = {"liek", "lik"};
        final String[] the = {"teh", "dah"};
        final String[] why = {"y", "wai"};
        final String[] love = {"lov", "luv", "lurv", "lovee"};

        //dictionary on base. :)
        final HashMap<String, String> dictionary = new HashMap<String, String>();
        dictionary.put("i am forever alone", "don't worry, so are thelegend27, bossman and i. :)");
        final String[] feelings = {"happy", "sad", "enthusiastic", "nervous", "good", "worried", "ecstatic", "scared", "laughing", "dying", "pumped", "tired", "proud", "crying", "amazing", "hurt"};

        //dictionary is now going to be on Firebase!
        Firebase.setAndroidContext(MainActivity.this);
        final Firebase sadServer = new Firebase("https://collaborative-chatbot.firebaseio.com/");

        //bulk of display
        final ArrayList<String> name = new ArrayList<String>();
        final ArrayList<String> conversation = new ArrayList<String>();
        final ArrayList<String> time = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "Jun","Jul", "Aug","Sep","Oct","Nov","Dec"};
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);
        final int second = cal.get(Calendar.SECOND);
        //this by no means works on leap years but I don't have to change it during my time at exeter :) i'm so edgy.
        time.add(monthName[month] + " " + dayOfMonth+ ", " + hour+":"+minute+":"+second);
        name.add(botName);
        conversation.add("Hey there! I'm Codfish Chatbot. I'm looking forward to talk to you! Unfortunately, I don't know many phrases or responses. Therefore, when I hear something I haven't heard before, I'll ask you how to respond.");
        time.add(monthName[month] + " " + dayOfMonth+ ", " + hour+":"+minute+":"+second);
        name.add(botName);
        conversation.add("You may also change my personality if you so wish! I am currently in a default setting. Simply type: @moodChange:happy to make me happier, or @moodChange:default to make me normal again.");
        final ListView textDisplay = (ListView) findViewById(R.id.chatLayout);
        final CustomAdapter theConversation = new CustomAdapter(MainActivity.this, name, conversation);
        textDisplay.setAdapter(theConversation);

        final EditText input = (EditText) findViewById(R.id.editText);
        final Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //what u said
                String pushPhrase = input.getText().toString();
                if (pushPhrase.length() > 0) {
                    if (name.size() % 2 == 0 && conversation.size() %2 == 0) {
                        if (!learning[0]) {
                            name.add(user);
                        }
                        else {
                            name.add("[SUGGESTION]");
                        }
                        conversation.add(pushPhrase);
                        Calendar cal = Calendar.getInstance();
                        final int hour = cal.get(Calendar.HOUR_OF_DAY);
                        final int minute = cal.get(Calendar.MINUTE);
                        final int second = cal.get(Calendar.SECOND);
                        time.add(monthName[month] + " " + dayOfMonth+ ", " + hour+":"+minute+":"+second);
                    }
                    //analyze
                    String analyze = pushPhrase.toLowerCase();
                    analyze = analyze.replace(",", "");
                    analyze = analyze.replace(".", "");
                    analyze = analyze.replace("'", "");
                    analyze = analyze.replace(";", "");
                    analyze = analyze.replace("!", "");
                    analyze = analyze.replace(":", "");
                    analyze = analyze.replace("?", "");
                    String[] inputWords = analyze.split(" ");

                    inputWords = correctSpelling(inputWords, what, like, why, the, you, love);

                    //get rid of all dat spelling mistakes howboudah
                    analyze = inputWords[0];
                    for (int i = 1; i<inputWords.length; i++) {
                        analyze = analyze + " " + inputWords[i];
                    }

                    //checks if he has responded before.
                    if (!name.get(name.size()-1).equals(botName)) {
                        name.add(botName);
                        Calendar cal = Calendar.getInstance();
                        final int hour = cal.get(Calendar.HOUR_OF_DAY);
                        final int minute = cal.get(Calendar.MINUTE);
                        final int second = cal.get(Calendar.SECOND);
                        time.add(monthName[month] + " " + dayOfMonth+ ", " + hour+":"+minute+":"+second);
                        if (!learning[0]) {
                            if (catchSwearException(analyze, doNotTeach) || catchStringSwearException(analyze, doNotTeach)) {
                                if (swearCount[0] == 0) {
                                    conversation.add("woah! maybe you can chill a bit...");
                                } else {
                                    Random r = new Random();
                                    String[] warning = {"Could you stop swearing?", "do you need me to clear your thoughts?", "as I said before, you should stop swearing...", "stop being so inappropriate", "I really shouldn't bother talking to ppl like you...", "stop using that word!"};
                                    int whichMessage = r.nextInt(warning.length);
                                    conversation.add(warning[whichMessage]);
                                }
                                swearCount[0]++;
                            }
                            else if (pushPhrase.equals("@moodChange:happy")){
                                if (personality[0] != "happy") {
                                    personality[0] = "happy";
                                    conversation.add("Okay! I now have a bubbly, happy personality. Talk to me again! :)");
                                }
                                else {
                                    conversation.add("I already am happy! No need to use this command.");
                                }
                            }
                            else if (pushPhrase.equals("@moodChange:serena")){
                                if (personality[0] != "serena") {
                                    personality[0] = "serena";
                                    conversation.add("okay now i will try to sound like serena");
                                }
                                else {
                                    conversation.add("I already am happy! No need to use this command.");
                                }
                            }
                            else if (pushPhrase.equals("@moodChange:default")){
                                if (personality[0] != "default") {
                                    personality[0] = "default";
                                    conversation.add("Okay! My personality is now normal. :)");
                                }
                                else {
                                    conversation.add("I am already in a neutral mood. No need to use this command.");
                                }
                            }
                            else {
                                String preprogrammed = preResponse(inputWords, feelings);

                                    String checkDic = String.valueOf(dictionary.get(analyze));
                                    if (checkDic != "null") {
                                        conversation.add(checkDic);
                                    }
                                    else if (preprogrammed.length() > 0) {
                                        conversation.add(preprogrammed);
                                    }
                                    else if (analyze.indexOf("teach you") != -1 || analyze.indexOf("teach u") != -1) {
                                        conversation.add("Why, of course! Tell me any phrase!");
                                        learning[0] = true;
                                        initiative[0] = true;
                                    }

                                    else {
                                        learning[0] = true;
                                        Firebase theOnlineDic = sadServer.child(personality[0]);
                                        Firebase reference = theOnlineDic.child(analyze);
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String response = dataSnapshot.getValue(String.class);
                                                if (response != null) {
                                                    if (response.length() > 0) {
                                                        conversation.add(response);
                                                        name.add(botName);
                                                        int remove = name.size() - 2;
                                                        name.remove(remove);
                                                        conversation.remove(remove);
                                                        theConversation.notifyDataSetChanged();
                                                        learning[0] = false;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(FirebaseError firebaseError) {
                                            }
                                        });
                                        //teach time!
                                            if (learning[0]) {
                                                conversation.add("I haven't heard that before! How should I respond to that?");
                                                key[0] = analyze;
                                            }
                                    }

                            }
                        } else if (learning[0]) {
                            if (catchSwearException(analyze, doNotTeach)) {
                                conversation.add("No! I refuse to learn that. You have another chance. >:(");
                            }
                            else if (initiative[0]) {
                                //this is where it learns.
                                key[0] = analyze;
                                Random r = new Random();
                                final Firebase theOnlineDic = sadServer.child(personality[0]);
                                Firebase reference = theOnlineDic.child(analyze);
                                String[] learnt = {"Okay, so how do I respond?", "Pretty interesting, what should my response be?", "Ooh, I like the ring to that! What should I say in response?"};
                                int whichMessage = r.nextInt(learnt.length);
                                conversation.add(learnt[whichMessage]);
                                initiative[0] = false;
                            }
                            else if (!initiative[0]) {
                                //this is where it learns.
                                dictionary.put(key[0], pushPhrase);
                                Random r = new Random();
                                String[] learnt = {"Okay, learn something every day, eh!", "Thanks. I'll use that next time when I hear it.", "That's pretty interesting! Back to the conversation :>", "That's a great response! Let me use that in the future."};
                                int whichMessage = r.nextInt(learnt.length);
                                conversation.add(learnt[whichMessage]);
                                learning[0] = false;
                                Firebase onlineDictionary = sadServer.child(personality[0]).child(key[0]);
                                onlineDictionary.setValue(pushPhrase);

                            }

                        }
                        theConversation.notifyDataSetChanged();
                        //clears the input place
                        input.setText("");
                    }
                }
            }
        });
        textDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder ab;
                ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("text: " + name.get(position) + " at " + time.get(position));
                ab.setMessage(conversation.get(position));

                ab.setNegativeButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog a = ab.create();
                a.show();
            }
        });
    }

    public boolean catchSwearException(String input, String[] badWords) {
        //first - check for bad words:
            for (int i = 0;i<badWords.length; i++) {
                if (input.indexOf(badWords[i]) != -1) {
                    return true;
                }
            }
        return false;
    }

    public boolean catchStringSwearException(String input, String[] badWords) {
        //first - check for bad words:
        input = input.replace(" ", "");
        for (int i = 0;i<badWords.length; i++) {
            if (input.indexOf(badWords[i]) != -1) {
                return true;
            }
        }
        return false;
    }

    public String[] correctSpelling (String[] a, String[] what, String[] like, String[] why, String[] the, String[] you, String[] love) {
        for (int i = 0; i<a.length; i++) {
            for (int j = 0;j<what.length;j++) {
                if (a[i].equals(what[j])) {
                    a[i] = "what";
                }
            }
            for (int j = 0;j<like.length;j++) {
                if (a[i].equals(like[j])) {
                    a[i] = "like";
                }
            }
            for (int j = 0;j<why.length;j++) {
                if (a[i].equals(why[j])) {
                    a[i] = "why";
                }
            }
            for (int j = 0;j<the.length;j++) {
                if (a[i].equals(the[j])) {
                    a[i] = "the";
                }
            }
            for (int j = 0;j<you.length;j++) {
                if (a[i].equals(you[j])) {
                    a[i] = "you";
                }
            }
            for (int j = 0;j<love.length;j++) {
                if (a[i].equals(love[j])) {
                    a[i] = "love";
                }
            }
        }
        return a;
    }


    public String preResponse(String[] input, String[] feelings) {
        if ((input[0].equals("i") && input[1].equals("am"))) {
            for (int i = 0; i<feelings.length; i++) {
                if (input[2].equals(feelings[i])) {
                    if (i%2 == 0) {
                        return "Glad you feel that way!";
                    }
                    return "Aww. I hope you feel better soon. :)";
                }
            }
            if (input[2].equals("called") && input[3].length()>0) {
                return "Nice to meet you, " + input[3].substring(0,1).toUpperCase()+input[3].substring(1) + "!";
            }
            if (input[2].length() > 0) {
                return "Nice to meet you, " + input[2].substring(0, 1).toUpperCase() + input[2].substring(1) + "!";
            }
            return "you are...?";
        }
        if (input[0].equals("yes")) {
            String[] resChoice= {"ahh.. okay.", "hmm okay then", "i see.", "ok!"};
            Random r = new Random();
            int selResponse = r.nextInt(resChoice.length);
            return resChoice[selResponse];
        }
        if (input[0].equals("no")) {
            String[] resChoice= {"hmm. okay.", "no what?", "i see.", "not a response i was expecting."};
            Random r = new Random();
            int selResponse = r.nextInt(resChoice.length);
            return resChoice[selResponse];
        }
        if (input[0].equals("hi") || input[0].equals("hey") || input[0].equals("hello") || input[0].equals("ey") || input[0].equals("hai")) {
            String[] greetings = {"hey there!", "hi :>", "eyy helloo", "oh hey!", "hello :)", "hai :)", "oh hi", "uh hi?"};
            Random r = new Random();
            int selResponse = r.nextInt(greetings.length);
            return greetings[selResponse];
        }
        if (input[0].equals("i")) {
            if (input[1].equals("like")) {
                if (input[2].equals("you")) {
                    return "i like you too!";
                }
                if (input[2].equals("myself")) {
                    String[] resChoice= {"that's cool, wish i could like myself :<", "okay settle down, Mr. Narcissist!"};
                    Random r = new Random();
                    int selResponse = r.nextInt(resChoice.length);
                    return resChoice[selResponse];
                }
            }
            else if (input[1].equals("dont")) {
                if (input[2].equals("like")) {
                    if (input[3].equals("you")) {
                        return "qq :'( why you gotta be so rude";
                    }
                    if (input[3].equals("myself")) {
                        return "that isn't too good, do you need help?";
                    }
                }

            }
        }
        return "";
    }
}
