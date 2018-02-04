package com.example.wishnu.anagramhasnoanagrams;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static android.R.id.edit;

public class GameActivity extends AppCompatActivity {

    private HelperActivity helper;
    private String currentQuestion;
    private List<String> anagrams;
    private int anagramModeFlag = 0, wordModeFlag = 1, score=0, level=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        final Button controlButton = (Button) findViewById(R.id.controlButton);
        final Button anagramModeButton = (Button) findViewById(R.id.anagramMode);
        final Button wordModeButton = (Button) findViewById(R.id.wordMode);
        AssetManager fileManager = getAssets();
        try {
            InputStream iStream = fileManager.open("words.txt");
            helper = new HelperActivity(new InputStreamReader(iStream));
        }catch (IOException e){
            Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG).show();
        }
        final EditText answerField = (EditText) findViewById(R.id.answerField);
        answerField.setEnabled(false);
        answerField.setRawInputType(InputType.TYPE_CLASS_TEXT);
        answerField.setImeOptions(EditorInfo.IME_ACTION_GO);
        answerField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {//when go key is pressed on keyboard
                    processWord(answerField);
                    handled = true;
                }
                return handled;
            }
        });

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView instructionView = (TextView) findViewById(R.id.instructionView);
                TextView resultView = (TextView) findViewById(R.id.resultView);
                EditText answerField = (EditText) findViewById(R.id.answerField);
                Button anagramModeButton = (Button) findViewById(R.id.anagramMode);
                Button wordModeButton = (Button) findViewById(R.id.wordMode);
                TextView scoreView = (TextView) findViewById(R.id.scoreView);
                TextView levelView = (TextView) findViewById(R.id.levelView);
                anagramModeButton.setVisibility(View.INVISIBLE);
                wordModeButton.setVisibility(View.INVISIBLE);
                if (currentQuestion == null) {
                    resultView.setText("");
                    level++;
                    scoreView.setText("SCORE:"+String.valueOf(score));
                    levelView.setText("LEVEL:"+String.valueOf(level));
                    currentQuestion = helper.pickGoodStarterWord(anagramModeFlag);

                    if (anagramModeFlag == 0) {
                        anagrams = helper.getGoodAnagrams(currentQuestion);
                        instructionView.setText("Find anagrams: " + (currentQuestion.toUpperCase())+" ["+anagrams.size()+"]");
                    } else if (anagramModeFlag == 1) {
                        anagrams = helper.getGoodAnagramsWithOneMoreWord(currentQuestion);
                        instructionView.setText("Find anagrams with ONE more letter: " + (currentQuestion.toUpperCase())+" ["+anagrams.size()+"]");
                    } else if (anagramModeFlag == 2) {
                        anagrams = helper.getGoodAnagramsWithTwoMoreWords(currentQuestion);
                        instructionView.setText("Find anagrams with TWO more letters: " + (currentQuestion.toUpperCase())+" ["+anagrams.size()+"]");
                    }
                    if(!answerField.isEnabled())
                        answerField.setText("");
                        answerField.setEnabled(true);
                    controlButton.setVisibility(View.INVISIBLE);

                }
                else{//User asks help
                    answerField.setText(currentQuestion.toUpperCase());//show question on edittext since instructionView has other content
                    answerField.setEnabled(false);
                    controlButton.setText(">");
                    currentQuestion = null;
                    resultView.append(TextUtils.join("\n", anagrams));
                    instructionView.setText(" Hit 'Play' to start again");
                }

            }
        });

        anagramModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(anagramModeFlag == 2)
                    anagramModeFlag = 0;
                else
                    anagramModeFlag++;
                anagramModeButton.setText(String.valueOf(anagramModeFlag));
            }
        });

        wordModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wordModeFlag==1) {
                    wordModeFlag = 2;
                    wordModeButton.setText("TWO");
                }
                else {
                    wordModeFlag = 1;
                    wordModeButton.setText("ONE");
                }
            }
        });

    }

    //function to color words and view results
    public void processWord(EditText editText){
        TextView resultView = (TextView) findViewById(R.id.resultView);
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        String word = editText.getText().toString().trim().toLowerCase();
        if (word.length() == 0) {
            return;
        }
        String color = "#cc0029";
        if(helper.isGoodWord(word, currentQuestion) && anagrams.contains(word)){
            score=score+level;
            scoreView.setText("SCORE:"+String.valueOf(score));
            anagrams.remove(word);
            color = "#00aa29";
        }
        else{
            word = word+" X";
        }
        resultView.append(Html.fromHtml(String.format("<font color=%s>%s</font><BR>", color, word)));//cant "append" with different colors in other way
        editText.setText("");
        Button controlButton = (Button) findViewById(R.id.controlButton);
        controlButton.setText("?");
        controlButton.setVisibility(View.VISIBLE);
    }
}
