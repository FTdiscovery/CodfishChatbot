package com.saltstudios.ftdiscovery.collaborativechatbot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gordon on 13/11/16.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private ArrayList<String> text;


    public CustomAdapter(Context dinner, ArrayList<String> name, ArrayList<String> t) {
        super(dinner, R.layout.custom_row,name);
        text = t;
    }



    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater foodInflater = LayoutInflater.from(getContext());
        View customView = foodInflater.inflate(R.layout.custom_row, parent,false);
        //Displays
        TextView nameDisplay = (TextView) customView.findViewById(R.id.name);
        TextView dialogueDisplay = (TextView) customView.findViewById(R.id.conversation);

        if(text.size()>0) {
            String name = getItem(position);
            String dialogue = text.get(position);
            nameDisplay.setText(name+":");
            dialogueDisplay.setText(dialogue);
            int baseHeight = 140;
            /* green colour as shown here is ugly plz change.
            if (name == "You") {
                nameDisplay.setBackgroundColor(Color.GREEN);
                dialogueDisplay.setBackgroundColor(Color.GREEN);
            }
            else {
                nameDisplay.setBackgroundColor(Color.WHITE);
                dialogueDisplay.setBackgroundColor(Color.WHITE);
            }
            */
            if (dialogue.length() >= 45) {
                nameDisplay.getLayoutParams().height = baseHeight + dialogue.length();
                dialogueDisplay.getLayoutParams().height = baseHeight + dialogue.length();
            }
            else {
                nameDisplay.getLayoutParams().height = baseHeight;
                dialogueDisplay.getLayoutParams().height = baseHeight;
            }
        }
        return customView;
    }

}
