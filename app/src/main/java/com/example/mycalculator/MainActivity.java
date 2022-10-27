package com.example.mycalculator;

import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity {

    Button division,fois,moins,plus;
    TextView result,calculating;
    String calcul;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the app to be fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize the SharedPreferences and their editor
        sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = sharedpreferences.edit();

        // Define Hooks
        // Operators' buttons
        division= findViewById(R.id.division);
        fois= findViewById(R.id.multiply);
        moins= findViewById(R.id.minus);
        plus= findViewById(R.id.plus);
        // Result and Current Calculations TextViews
        result= findViewById(R.id.result);
        calculating= findViewById(R.id.calcul);

    }

    public void onButtonClicked(View view) {

        Button button = (Button) view;
        String data = button.getText().toString(); // The tapped Button's Value

        calcul= calculating.getText().toString();

        switch (data) {
            case "AC":
                calculating.setText("");
                result.setText("");
                break;

            case "CE":
                if(!calcul.isEmpty())
                {
                    calculating.setText(calcul.subSequence(0,calcul.length()-1));
                }
                break;

            case "(":
                //checking();
                //calculating.setText(calculating.getText()+puissance.getText().toString());
                result.setText("");
                Parantheses("(");
                break;

            case "*":
                checking();
                result.setText("");
                calculating.setText(calculating.getText()+fois.getText().toString());
                break;

            case "+":
                checking();
                result.setText("");
                calculating.setText(calculating.getText()+plus.getText().toString());
                break;

            case "-":
                checking();
                result.setText("");
                calculating.setText(calculating.getText()+moins.getText().toString());
                break;

            case "/":
                checking();
                result.setText("");
                calculating.setText(calculating.getText()+division.getText().toString());
                break;

            case ".":
                if (calcul.isEmpty())
                {
                    calculating.setText("0.");
                }
                else
                {
                    checking();
                    result.setText("");
                    calculating.setText(calculating.getText() + ".");
                }

                break;

            case ")":
                /*checking();
                calculating.setText(calculating.getText()+pourcent.getText().toString());
                double d = Double.parseDouble(calculating.getText().toString().substring(0,calculating.getText().toString().length()-1)) / 100;
                result.setText(String.valueOf(d));
                calculating.setText(String.valueOf(d));*/
                result.setText("");
                Parantheses(")");
                break;

            case "=":
                result.setText("");
                calculating.setText(solve(calcul));
                break;

            default:
                calculating.setText(calcul+data);
                if((calcul.length()>1)&&(!solve(calcul+data).equals("ERROR")))
                result.setText(solve(calcul+data));

        }

    }

    private void Parantheses(String p){
        if(calculating.getText().length()>0) {
            if(!String.valueOf(calcul.charAt(calcul.length()-1)).equals(p)){
                calculating.setText(calculating.getText()+p);
            }
        }else if ((calculating.getText().length()==0) && (p.equals("("))){
            calculating.setText(p);
        }
    }

    private void checking() {
        String last="";
        if(calculating.getText().length()>0) {
            last = String.valueOf(calculating.getText().charAt(calculating.getText().length() - 1));
        }
        if((last.equals("/"))||(last.equals("+"))||(last.equals("-"))||(last.equals("*"))||(last.equals("^"))||(last.equals("%"))||(last.equals(".")))
        {
            calculating.setText(calculating.getText().subSequence(0,calculating.getText().length()-1));
        }
    }

    private String solve(String data) {
        Log.d("data before : ",data);

        if(String.valueOf(data.charAt(data.length()-1)).matches("[+-/*.]")){
            data=data.substring(0,data.length()-1);
        }
        Log.d("data after : ",data);
        try{
            Context context  = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String finalResult =  context.evaluateString(scriptable,data,"Javascript",1,null).toString();


            if(finalResult.endsWith(".0")){
                finalResult = finalResult.replace(".0","");
            }

            if(finalResult.equals("Infinity")){
                callToast("Can't Divide By Zero");
                return "";
            }

            if (calcul.split("\\^").length == 2) {
                String numbers[] = calcul.split("\\^");
                try {
                    double d = Math.pow(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]));
                    //result.setText(Double.toString(d));
                    //calculating.setText(Double.toString(d));
                    finalResult=Double.toString(d);
                }catch (Exception e){
                    callToast(e.getMessage());
                }
            }
            return finalResult;
        }catch (Exception e){
            callToast(e.getMessage());
            return "ERROR";
        }
    }



    private void callToast(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //callToast("onPause");

        editor.putString("result", result.getText().toString()); // save the result's value
        editor.putString("calculating", calculating.getText().toString()); // save the calculation value
        editor.commit(); // commit changes

    }

    @Override
    protected void onResume() {
        super.onResume();

        //callToast("onResume");

        result.setText(sharedpreferences.getString("result", "")); // getthe result's value
        calculating.setText(sharedpreferences.getString("calculating", "")); // get the calculation's value

        //callToast(sharedpreferences.getString("result", ""));
        //callToast(sharedpreferences.getString("calculating", ""));

        editor.clear(); // clear stored data
        editor.commit(); // commit changes


    }
}